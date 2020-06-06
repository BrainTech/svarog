package org.signalml.app.action.tag;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker.StateValue;
import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.tag.synchronize.SynchronizeTagsWithTriggerDialog;
import org.signalml.app.worker.signal.FindSignalSlopesWorker;
import org.signalml.app.worker.signal.SynchronizeTagsWithTriggerWorker;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * An action for synchronizing tags with a trigger channel.
 *
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerAction extends TagDocumentModificationAction implements PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(SynchronizeTagsWithTriggerAction.class);

	private SynchronizeTagsWithTriggerParameters parameters;
	private SynchronizeTagsWithTriggerDialog synchronizeDialog;

	private FindSignalSlopesWorker findSlopesWorker;
	private SynchronizeTagsWithTriggerWorker synchronizeTagsWorker;

	public SynchronizeTagsWithTriggerAction(TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(tagDocumentFocusSelector);
		setText(_("Synchronize tags with trigger"));
	}

	public SynchronizeTagsWithTriggerDialog getSynchronizeDialog() {
		if (synchronizeDialog == null) {
			synchronizeDialog = new SynchronizeTagsWithTriggerDialog();
		}
		return synchronizeDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		parameters = new SynchronizeTagsWithTriggerParameters();
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();

		SignalPlot masterPlot = signalDocument.getSignalView().getMasterPlot();
		parameters.setSampleSource(masterPlot.getSignalOutput());
		parameters.setTagSet(signalDocument.getActiveTag().getTagSet());
		parameters.setChannelLabels(getChannelLabels(masterPlot.getSignalOutput()));

		boolean showDialog = getSynchronizeDialog().showDialog(parameters, true);

		if (!showDialog) {
			return;
		}

		findSlopesWorker = new FindSignalSlopesWorker(parameters);
		findSlopesWorker.addPropertyChangeListener(this);
		findSlopesWorker.execute();

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource() == findSlopesWorker && evt.getNewValue() == StateValue.DONE) {
			Integer[] slopes;
			try {
				slopes = findSlopesWorker.get();

				int tagCount = parameters.getTagSet().getTagCount();

				synchronizeTagsWorker = new SynchronizeTagsWithTriggerWorker(parameters, slopes);
				synchronizeTagsWorker.execute();
				synchronizeTagsWorker.get();

				if (slopes.length < tagCount) {
					Dialogs.showWarningMessage(_R("There were more tags ({0}) than activating trigger slopes ({1}). Excessive tags have been removed!", tagCount, slopes.length));
				} else if (slopes.length > tagCount) {
					Dialogs.showWarningMessage(_R("There were less tags ({0}) than activating trigger slopes ({1}).", tagCount, slopes.length));
				} else {
					Dialogs.showMessage(_("Tags Synchronized"), _("Tags were successfully synchronized with the trigger channel."));
				}

			} catch (InterruptedException e) {
				logger.error("", e);
			} catch (ExecutionException e) {
				logger.error("", e);
			}
		}
	}

	protected List<String> getChannelLabels(MultichannelSampleSource sampleSource) {

		List<String> channelLabels = new ArrayList<>();

		for (int i = 0; i < sampleSource.getChannelCount(); i++) {
			channelLabels.add(sampleSource.getLabel(i));
		}
		return channelLabels;

	}

}
