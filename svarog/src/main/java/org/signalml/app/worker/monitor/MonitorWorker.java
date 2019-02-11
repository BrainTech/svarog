package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.signal.PagingParameterDescriptor;
import org.signalml.app.view.common.dialogs.BusyDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.common.dialogs.errors.Dialogs.DIALOG_OPTIONS;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource;
import org.signalml.domain.tag.MonitorTag;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.domain.tag.TagStylesGenerator;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.FormatUtils;

import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.SignalMsg;
import org.signalml.app.worker.monitor.messages.IncompleteTagMsg;
import org.signalml.peer.Peer;
import org.signalml.psychopy.PsychopyStatusListener;

/** MonitorWorker
 *
 */
public class MonitorWorker extends SwingWorkerWithBusyDialog<Void, Object> {

	public static String OPENING_MONITOR_CANCELLED = "openingMonitorCanceled";
	public static final int TIMEOUT_MILIS = 5000;
	protected static final Logger logger = Logger.getLogger(MonitorWorker.class);

	private final Peer peer;
	private final RoundBufferMultichannelSampleSource sampleSource;
	private final StyledMonitorTagSet tagSet;
	private volatile boolean finished;

	/**
	 * This object's method is called whenever Psychopy experiment status changes.
	 */
	private final Set<PsychopyStatusListener> psychopyStatusListeners = new HashSet<>();

	/**
	 * Styles generator for monitor tags.
	 */
	private TagStylesGenerator stylesGenerator;

	private ExperimentDescriptor experimentDescriptor;

	private final Map<String, MonitorTag> tagsPerID;

	public MonitorWorker(ExperimentDescriptor experimentDescriptor, RoundBufferMultichannelSampleSource sampleSource, StyledMonitorTagSet tagSet) {
		super(null);
		this.experimentDescriptor = experimentDescriptor;
		this.peer = experimentDescriptor.getPeer();
		this.sampleSource = sampleSource;
		this.tagSet = tagSet;
		this.tagsPerID = new HashMap<>();

		//TODO blocksPerPage - is that information sent to the monitor worker? Can we substitute default PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE
		//with a real value?
		stylesGenerator = new TagStylesGenerator(experimentDescriptor.getSignalParameters().getPageSize(), PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE);
		getBusyDialog().setText(_("Waiting for the first sample..."));

		logger.setLevel((Level) Level.INFO);
	}

	public void acceptUserTag(MonitorTag tag) {
		process(Collections.singletonList(tag));
	}

	@Override
	protected Void doInBackground() {

		BaseMessage sampleMsg;
		boolean firstSampleReceived = false;
		showBusyDialog();

		peer.subscribe(MessageType.AMPLIFIER_SIGNAL_MESSAGE.getMessageCode());
		peer.subscribe(MessageType.TAG_MSG.getMessageCode());
		peer.subscribe(MessageType.INCOMPLETE_TAG_MSG.getMessageCode());
		peer.subscribe(MessageType.PSYCHOPY_EXPERIMENT_ERROR.getMessageCode());
		peer.subscribe(MessageType.PSYCHOPY_EXPERIMENT_FINISHED.getMessageCode());
		peer.subscribe(MessageType.PSYCHOPY_EXPERIMENT_STARTED.getMessageCode());

		while (!isCancelled()) {
			try {

				if (firstSampleReceived)
					sampleMsg = peer.receive(TIMEOUT_MILIS);
				else {
					/**
					 * The first sample to be received is the most problematic
					 * - sometimes (when starting new experiments) the multiplexer
					 * has not been started yet at this point, so the receive
					 * timeout should longer in order to wait for the multiplexer
					 * to fully start to operate.
					 */
					sampleMsg = peer.receive();
					firstSampleReceived = true;
					hideBusyDialog();
				}
				if (isCancelled()) {
					//it might have been cancelled while waiting on the client.receive()
					return null;
				}
				else if (sampleMsg == null) {
					// timeout or error

					double timeoutInSeconds = ((double)TIMEOUT_MILIS) / 1000.0;
					DIALOG_OPTIONS result = Dialogs.showWarningYesNoDialog(_R("Did not receive any samples in {0} seconds, maybe the experiment is down?\nDo you wish to wait some more? Press No to disconnect from the experiment.", FormatUtils.format(timeoutInSeconds)));
					if (result == DIALOG_OPTIONS.YES) {
						continue;
					} else {
						DisconnectFromExperimentWorker disconnectWorker = new DisconnectFromExperimentWorker(experimentDescriptor);
						disconnectWorker.execute();
						break;
					}
				}
			} catch (OpenbciCommunicationException e) {
				logger.error("receive failed", e);
				return null;
			}
			
			MessageType sampleType = sampleMsg.getType();
			switch (sampleType) {
			case AMPLIFIER_SIGNAL_MESSAGE:
				publishSamples(sampleMsg);
				break;
			case INCOMPLETE_TAG_MSG:
			case TAG_MSG:
				publishTagFromMessage(sampleMsg);
				break;
			case PSYCHOPY_EXPERIMENT_ERROR:
			case PSYCHOPY_EXPERIMENT_FINISHED:
			case PSYCHOPY_EXPERIMENT_STARTED:
				notifyPsychopyExperimentStatusChange(sampleMsg);
				break;
			default:
				logger.error("received unknown reply: " +  sampleType);
			}
		}

		return null;
	}

	/**
	 * If the message contains samples, this function can be used
	 * to parse its contents.
	 * @param sampleMsgData the content of the message
	 */
	protected void publishSamples(BaseMessage generic_message) {
		SignalMsg sampleMsg  = (SignalMsg)generic_message;
		List<NewSamplesData> samples = sampleMsg.getSamples();
		
		for(NewSamplesData sample: samples){
			publish(sample);
		}

	}

	/**
	 * If the given message contains tags, this method can be used
	 * to parse its contents and publish it to gui, recorder etc.
	 * @param tagMsg tagMessage containing tag
	 */
	private void publishTagFromMessage(BaseMessage msg) {
		
		logger.info("Tag recorder: got a tag!");

		IncompleteTagMsg tagMsg = (IncompleteTagMsg) msg;
		// TODO: By now we ignore field channels and assume that tag if for all channels
		double tagLen = tagMsg.getDuration();

		TagStyle style = tagSet.getStyle(SignalSelectionType.CHANNEL, tagMsg.getName());

		if (style == null) {
			style = stylesGenerator.getSmartStyleFor(tagMsg.getName(), tagLen, -1);
			tagSet.addStyle(style);
		}
		int channel = -1;
		try{
		    channel = Integer.parseInt(tagMsg.getChannels());
		}
		catch(NumberFormatException e){}

		final MonitorTag tag = new MonitorTag(style,
				tagMsg.getStartTimestamp(),
				tagLen,
				channel,
				tagMsg.getID()
		);

		tagMsg.getDescription().forEach((key, value) -> {     
			if (key.equals("annotation")) {
				tag.setAnnotation(value.toString());
			}
			else {
				tag.addAttributeToTag(key, value.toString());
			}
		
		
		});

		publish(tag);
	}

	private void notifyPsychopyExperimentStatusChange(BaseMessage msg){
		Set<PsychopyStatusListener> listeners;
		synchronized (psychopyStatusListeners) {
			listeners = new HashSet<>(psychopyStatusListeners);
		}
		for (PsychopyStatusListener listener : listeners) {
			SwingUtilities.invokeLater(() -> {
				listener.psychopyStatusChanged(msg);
			});
		}
	}
	
	@Override
	protected void process(List<Object> objs) {
		for (Object o : objs) {
			if (o instanceof NewSamplesData) {

				NewSamplesData data = (NewSamplesData) o;

				sampleSource.lock();
				tagSet.lock();
				sampleSource.addSamples(data);
				tagSet.newSample(data.getSamplesTimestamp());
				tagSet.unlock();
				sampleSource.unlock();

			} else if (o instanceof MonitorTag) {
				MonitorTag tag = (MonitorTag) o;

				String id = tag.getID();
				MonitorTag previous = null;
				if (id != null && !id.isEmpty()) {
					synchronized (tagsPerID) {
						previous = tagsPerID.get(id);
						if (previous != null && previous.isComplete()) {
							// only incomplete tags can be updated
							logger.warn("received update request for complete tag");
							return;
						}
						tagsPerID.put(id, tag);
					}
				}

				tagSet.lock();
				if (previous != null) {
					tagSet.updateTag(previous, tag);
				} else {
					tagSet.addTag(tag);
				}
				tagSet.unlock();

				//record tag
				firePropertyChange("newTag", null, (MonitorTag) o);
			} else {
				String className = (o == null) ? "null" : o.getClass().getSimpleName();
				logger.error("unsupported object ("+className+") passed to MonitorWorker");
			}
		}
	}

	@Override
	protected void done() {
		super.done();
		finished = true;
		firePropertyChange("tagsRead", null, tagSet);
	}

	public StyledMonitorTagSet getTagSet() {
		return tagSet;
	}

	public boolean isFinished() {
		return finished;
	}

	/**
	 * Connects a {@link PsychopyStatusListener} object to be notified
	 * whenever video recording starts or stops.
	 *
	 * @param listener  object with psychopyStatusChanged method
	 */
	public void connectPsychopyStatusListener(PsychopyStatusListener listener) {
		synchronized (psychopyStatusListeners) {
			psychopyStatusListeners.add(listener);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (BusyDialog.CANCEL_BUTTON_PRESSED.equals(evt.getPropertyName())) {
			cancel(true);
			firePropertyChange(MonitorWorker.OPENING_MONITOR_CANCELLED, false, true);
		}
	}
}
