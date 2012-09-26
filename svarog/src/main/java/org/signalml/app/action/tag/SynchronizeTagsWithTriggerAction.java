package org.signalml.app.action.tag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker.StateValue;

import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
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

		try {
			SignalPlot masterPlot = signalDocument.getSignalView().getMasterPlot();
			parameters.setSampleSource(masterPlot.getSignalOutput());
			parameters.setTagSet(signalDocument.getActiveTag().getTagSet());
			parameters.setChannelLabels(getChannelLabels(masterPlot.getSignalOutput()));
		} catch (InvalidClassException e1) {
			e1.printStackTrace();
			Dialogs.showExceptionDialog(e1);
			return;
		}

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
					Dialogs.showWarningMessage(_("There were more tags than activating trigger slopes. Excessive tags have been removed!"));
				} else if (slopes.length > tagCount) {
					Dialogs.showWarningMessage(_("There were less tags then activating trigger slopes."));
				} else {
					Dialogs.showMessage(_("Tags Synchronized"), _("Tags were successfully synchronized with the trigger channel."));
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	protected List<String> getChannelLabels(MultichannelSampleSource sampleSource) {

		List<String> channelLabels = new ArrayList<String>();

		for (int i = 0; i < sampleSource.getChannelCount(); i++) {
			channelLabels.add(sampleSource.getLabel(i));
		}
		return channelLabels;

	}

}
