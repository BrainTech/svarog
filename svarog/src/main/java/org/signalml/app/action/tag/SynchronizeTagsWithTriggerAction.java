package org.signalml.app.action.tag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.tag.synchronize.SynchronizeTagsWithTriggerDialog;
import org.signalml.app.worker.SynchronizeTagsWithTriggerWorker;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * An action for synchronizing tags with a trigger channel.
 *
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerAction extends TagDocumentModificationAction {

	private SynchronizeTagsWithTriggerDialog synchronizeDialog;

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
		SynchronizeTagsWithTriggerParameters model = new SynchronizeTagsWithTriggerParameters();
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();

		try {
			SignalPlot masterPlot = signalDocument.getSignalView().getMasterPlot();
			model.setSampleSource(masterPlot.getSignalOutput());
			model.setTagSet(signalDocument.getActiveTag().getTagSet());
			model.setChannelLabels(getChannelLabels(masterPlot.getSignalOutput()));
		} catch (InvalidClassException e1) {
			e1.printStackTrace();
			Dialogs.showExceptionDialog(e1);
			return;
		}

		boolean showDialog = getSynchronizeDialog().showDialog(model, true);

		if (!showDialog) {
			return;
		}

		SynchronizeTagsWithTriggerWorker worker = new SynchronizeTagsWithTriggerWorker(model);
		worker.execute();

	}

	protected List<String> getChannelLabels(MultichannelSampleSource sampleSource) {

		List<String> channelLabels = new ArrayList<String>();

		for (int i = 0; i < sampleSource.getChannelCount(); i++) {
			channelLabels.add(sampleSource.getLabel(i));
		}
		return channelLabels;

	}

}
