package org.signalml.app.view.tag.synchronize;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;

import javax.swing.JComponent;

import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;

/**
 * A dialog for selecting parameters for synchronizing tags
 * with trigger.
 *
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerDialog extends AbstractDialog {

	private SynchronizeTagsWithTriggerPanel synchronizePanel;

	public SynchronizeTagsWithTriggerDialog() {
		super();
		setTitle(_("Synchronize tags with trigger"));

		setMinimumSize(new Dimension(300, 120));
	}

	@Override
	protected JComponent createInterface() {
		return getSynchronizePanel();
	}

	public SynchronizeTagsWithTriggerPanel getSynchronizePanel() {
		if (synchronizePanel == null) {
			synchronizePanel = new SynchronizeTagsWithTriggerPanel();
		}
		return synchronizePanel;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SynchronizeTagsWithTriggerParameters.class.isAssignableFrom(clazz);
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		getSynchronizePanel().fillPanelFromModel((SynchronizeTagsWithTriggerParameters) model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		getSynchronizePanel().fillModelFromDialog((SynchronizeTagsWithTriggerParameters) model);
	}

}
