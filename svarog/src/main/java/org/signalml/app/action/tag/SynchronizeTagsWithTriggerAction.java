package org.signalml.app.action.tag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.app.view.tag.synchronize.SynchronizeTagsWithTriggerDialog;
import org.signalml.app.worker.SynchronizeTagsWithTriggerWorker;

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
		model.setSignalDocument(getActionFocusSelector().getActiveSignalDocument());

		boolean showDialog = getSynchronizeDialog().showDialog(model, true);

		if (!showDialog) {
			return;
		}

		SynchronizeTagsWithTriggerWorker worker = new SynchronizeTagsWithTriggerWorker(model);
		worker.execute();

		try {
			worker.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
