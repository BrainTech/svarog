package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.beans.PropertyChangeEvent;
import java.util.List;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.signal.PagingParameterDescriptor;
import org.signalml.app.view.common.dialogs.BusyDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.common.dialogs.errors.Dialogs.DIALOG_OPTIONS;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource;
import org.signalml.domain.tag.MonitorTag;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.domain.tag.TagStylesGenerator;
import org.signalml.multiplexer.protocol.SvarogConstants.MessageTypes;
import org.signalml.multiplexer.protocol.SvarogProtocol;
import org.signalml.multiplexer.protocol.SvarogProtocol.Sample;
import org.signalml.multiplexer.protocol.SvarogProtocol.SampleVector;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.FormatUtils;

import com.google.protobuf.ByteString;

/** MonitorWorker
 *
 */
public class MonitorWorker extends SwingWorkerWithBusyDialog<Void, Object> {

	public static String OPENING_MONITOR_CANCELLED = "openingMonitorCanceled";
	public static final int TIMEOUT_MILIS = 5000;
	protected static final Logger logger = Logger.getLogger(MonitorWorker.class);

	private final JmxClient client;
	private final RoundBufferMultichannelSampleSource sampleSource;
	private final StyledMonitorTagSet tagSet;
	private volatile boolean finished;

	/**
	 * This object is responsible for recording tags received by this {@link MonitorWorker}.
	 * It is connected to the monitor by an external object using
	 * {@link MonitorWorker#connectTagRecorderWorker(org.signalml.app.worker.monitor.TagRecorder)}.
	 */
	private TagRecorder tagRecorderWorker;

	/**
	 * This object is responsible for recording signal received by this {@link MonitorWorker}.
	 * It is connected to the monitor by an external object using
	 * {@link MonitorWorker#connectSignalRecorderWorker(org.signalml.app.worker.monitor.SignalRecorderWorker) }.
	 */
	private SignalRecorderWorker signalRecorderWorker;

	/**
	 * Styles generator for monitor tags.
	 */
	private TagStylesGenerator stylesGenerator;

	private ExperimentDescriptor experimentDescriptor;

	public MonitorWorker(ExperimentDescriptor experimentDescriptor, RoundBufferMultichannelSampleSource sampleSource, StyledMonitorTagSet tagSet) {
		super(null);
		this.experimentDescriptor = experimentDescriptor;
		this.client = experimentDescriptor.getJmxClient();
		this.sampleSource = sampleSource;
		this.tagSet = tagSet;

		//TODO blocksPerPage - is that information sent to the monitor worker? Can we substitute default PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE
		//with a real value?
		stylesGenerator = new TagStylesGenerator(experimentDescriptor.getSignalParameters().getPageSize(), PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE);
		getBusyDialog().setText(_("Waiting for the first sample..."));

		logger.setLevel((Level) Level.INFO);
	}

	@Override
	protected Void doInBackground() {

		MultiplexerMessage sampleMsg;
		boolean firstSampleReceived = false;
		showBusyDialog();

		while (!isCancelled()) {
			try {
				IncomingMessageData msgData;

				if (firstSampleReceived)
					msgData = client.receive(TIMEOUT_MILIS);
				else {
					/**
					 * The first sample to be received is the most problematic
					 * - sometimes (when starting new experiments) the multiplexer
					 * has not been started yet at this point, so the receive
					 * timeout should longer in order to wait for the multiplexer
					 * to fully start to operate.
					 */
					msgData = client.receive();
					firstSampleReceived = true;
					hideBusyDialog();
				}

				if (isCancelled()) {
					//it might have been cancelled while waiting on the client.receive()
					return null;
				}
				else if (msgData == null) {
					// timeout

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
				else {
					sampleMsg = msgData.getMessage();
				}
			} catch (InterruptedException e) {
				logger.error("receive failed", e);
				return null;
			}

			int sampleType = sampleMsg.getType();
			logger.debug("Worker: received message type: " + sampleType);

			switch (sampleType) {
			case MessageTypes.AMPLIFIER_SIGNAL_MESSAGE:
				parseMessageWithSamples(sampleMsg.getMessage());
				break;
			case MessageTypes.TAG:
				parseMessageWithTags(sampleMsg.getMessage());
				break;
			default:
				final String name = MessageTypes.instance.getConstantsNames().get(sampleType);
				logger.error("received unknown reply: " +  sampleType + "/" + name);
			}
		}

		return null;
	}

	/**
	 * If the message contains samples, this function can be used
	 * to parse its contents.
	 * @param sampleMsgString the content of the message
	 */
	protected void parseMessageWithSamples(ByteString sampleMsgString) {
		logger.debug("Worker: reading chunk!");

		SampleVector sampleVector;
		try {
			sampleVector = SampleVector.parseFrom(sampleMsgString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		List<Sample> samples = sampleVector.getSamplesList();

		for (int k=0; k<sampleVector.getSamplesCount(); k++) {
			Sample sample = sampleVector.getSamples(k);

			float[] newSamplesArray = new float[sample.getChannelsCount()];
			for (int i = 0; i < newSamplesArray.length; i++) {
				newSamplesArray[i] = sample.getChannels(i);
			}

			double samplesTimestamp = samples.get(0).getTimestamp();
			//System.out.println("  -- " + samplesTimestamp);
			NewSamplesData newSamplesPackage = new NewSamplesData(newSamplesArray, samplesTimestamp);

			publish(newSamplesPackage);
		}
	}

	/**
	 * If the given message contains tags, this method can be used
	 * to parse its contents.
	 * @param sampleMsgString the contents of the message
	 */
	private void parseMessageWithTags(ByteString sampleMsgString) {
		logger.info("Tag recorder: got a tag!");

		final SvarogProtocol.Tag tagMsg;
		try {
			tagMsg = SvarogProtocol.Tag.parseFrom(sampleMsgString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// TODO: By now we ignore field channels and assume that tag if for all channels
		final double tagLen = tagMsg.getEndTimestamp() - tagMsg.getStartTimestamp();

		TagStyle style = tagSet.getStyle(SignalSelectionType.CHANNEL, tagMsg.getName());

		if (style == null) {
			style = stylesGenerator.getSmartStyleFor(tagMsg.getName(), tagLen, -1);
			tagSet.addStyle(style);
		}

		final MonitorTag tag = new MonitorTag(style,
											  tagMsg.getStartTimestamp(),
											  tagLen,
											  -1);

		for (SvarogProtocol.Variable v : tagMsg.getDesc().getVariablesList()) {
			if (v.getKey().equals("annotation")) {
				tag.setAnnotation(v.getValue());
			}
			else {
				tag.addAttributeToTag(v.getKey(), v.getValue());
			}
		}
		publish(tag);
	}

	@Override
	protected void process(List<Object> objs) {
		for (Object o : objs) {
			if (o instanceof NewSamplesData) {

				NewSamplesData data = (NewSamplesData) o;

				sampleSource.lock();
				tagSet.lock();
				sampleSource.addSamples(data.getSampleValues());
				tagSet.newSample(data.getSamplesTimestamp());
				tagSet.unlock();
				sampleSource.unlock();

				// set first sample timestamp for the tag recorder
				if (tagRecorderWorker != null && !tagRecorderWorker.isStartRecordingTimestampSet()) {
					tagRecorderWorker.setStartRecordingTimestamp(data.getSamplesTimestamp());
				}

				// sends chunks to the signal recorder
				if (signalRecorderWorker != null) {
					signalRecorderWorker.offerChunk(data.getSampleValues());
					if (!signalRecorderWorker.isFirstSampleTimestampSet())
						signalRecorderWorker.setFirstSampleTimestamp(data.getSamplesTimestamp());
				}

			} else {
				MonitorTag tag = (MonitorTag) o;

				tagSet.lock();
				tagSet.addTag(tag);
				tagSet.unlock();

				//record tag

				if (tagRecorderWorker != null) {
					tagRecorderWorker.offerTag(tag);
				}

				firePropertyChange("newTag", null, (MonitorTag) o);
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
	 * Sets the {@link TagRecorder} to which the tags will be sent by this
	 * {@link MonitorWorker}. Setting a {@link TagRecorder} using this method
	 * starts sending all tags received by this {@link MonitorWorker} to the
	 * given {@link TagRecorder}.
	 *
	 * @param tagRecorderWorker the {@link TagRecorder} responsible for recording
	 * the tags from this {@link MonitorWorker}.
	 */
	public void connectTagRecorderWorker(TagRecorder tagRecorderWorker) {
		this.tagRecorderWorker = tagRecorderWorker;
	}

	/**
	 * Allows to disconnect a {@link TagRecorder} which was connected using
	 * {@link MonitorWorker#connectTagRecorderWorker(org.signalml.app.worker.monitor.TagRecorder)}.
	 * No more tags are sent to the {@link TagRecorder} after disconnecting.
	 */
	public void disconnectTagRecorderWorker() {
		this.tagRecorderWorker = null;
	}

	/**
	 * Sets the {@link SignalRecorderWorker} to which signal will be sent by this
	 * {@link MonitorWorker}. Setting a {@link SignalRecorderWorker} using this method
	 * starts sending signal received by this {@link MonitorWorker} to the
	 * given SignalRecorderWorker.
	 *
	 * @param signalRecorderWorker the {@link SignalRecorderWorker} responsible for recording
	 * signal from this {@link MonitorWorker}.
	 */
	public void connectSignalRecorderWorker(SignalRecorderWorker signalRecorderWorker) {
		this.signalRecorderWorker = signalRecorderWorker;
	}

	/**
	 * Disconnects a {@link SignalRecorderWorker} which was connected using
	 * {@link MonitorWorker#connectSignalRecorderWorker(org.signalml.app.worker.monitor.SignalRecorderWorker) }.
	 * Signal is no longer sent to the {@link SignalRecorderWorker} after disconnecting.
	 */
	public void disconnectSignalRecorderWorker() {
		this.signalRecorderWorker = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (BusyDialog.CANCEL_BUTTON_PRESSED.equals(evt.getPropertyName())) {
			cancel(true);
			firePropertyChange(MonitorWorker.OPENING_MONITOR_CANCELLED, false, true);
		}
	}

	/**
	 * Converts milliseconds to a String representing date
	 * - for debugging purposes
	public static String msToDate(double timestamp) {
		long mili = (long) (timestamp * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss,SSS");

		Date resultdate = new Date(mili);
		return(sdf.format(resultdate));
	}*/
}

/**
 * This class holds information about the newest samples package that was received
 * by Svarog and published by the doInTheBackground method.
 *
 * The data consists of the sample values (a sample value for each channel)
 * and a timestamp of these samples.
 * @author Piotr Szachewicz
 */
class NewSamplesData {
	/**
	 * The values of the samples. The size of the array is equal to the number
	 * of channels in the signal.
	 */
	private float[] sampleValues;

	/**
	 * The timestamp of the samples represented by the sampleValues array.
	 */
	private double samplesTimestamp;

	/**
	 * Constructor. Creates an object containing samples data.
	 * @param sampleValues the values of the samples for each channel
	 * @param samplesTimestamp the timestamp of the samples
	 */
	public NewSamplesData(float[] sampleValues, double samplesTimestamp) {
		this.sampleValues = sampleValues;
		this.samplesTimestamp = samplesTimestamp;
	}

	public float[] getSampleValues() {
		return sampleValues;
	}

	public void setSampleValues(float[] sampleValues) {
		this.sampleValues = sampleValues;
	}

	public double getSamplesTimestamp() {
		return samplesTimestamp;
	}

	public void setSamplesTimestamp(double samplesTimestamp) {
		this.samplesTimestamp = samplesTimestamp;
	}

}
