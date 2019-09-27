/* StartMonitorRecordingDialog.java created 2010-11-03
 *
 */

package org.signalml.app.view.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.worker.monitor.ObciServerCapabilities;
import org.signalml.plugin.export.SignalMLException;

/**
 * Represents a dialog shown when the user choose to start monitor recording.
 * Contains a panel to select files to which signal and tags will be recorded.
 *
 * @author Piotr Szachewicz
 */
public class StartMonitorRecordingDialog extends AbstractDialog {

	protected ChooseFilesForMonitorRecordingPanel chooseFilesForMonitorRecordingPanel;
	protected ChooseVideoForMonitorRecordingPanel chooseVideoForMonitorRecordingPanel;

	private JCheckBox saveImpedanceCheckBox;
	private JCheckBox appendTimestampsCheckBox;

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public StartMonitorRecordingDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Start recording"));
		super.initialize();
	}

	/**
	 * Creates the interface of this dialog.
	 * Contents of this interface depends on the implementation.
	 * @return the interface of this dialog
	 */
	@Override
	protected JComponent createInterface() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(getChooseFilesForMonitorRecordingPanel());
		if (ObciServerCapabilities.getSharedInstance().hasVideoSaving()) {
			panel.add(getChooseVideoForMonitorRecordingPanel());
		}
		// commented out until opening signal files with impedance works
//		panel.add(getSaveImpedanceCheckBox());
                JPanel subpanel = new JPanel();
		subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
                panel.add(subpanel);
                subpanel.add(getAppendTimestampsCheckBox());
                subpanel.add(chooseFilesForMonitorRecordingPanel.getEnableTagRecordingPanel());
		return panel;
	}

	/**
	 * Returns if the model can be of the given type.
	 * @param clazz the type of the model
	 * @return true the model can be of the given type, false otherwise
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ExperimentDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * Fills the fields of this dialog from the given model.
	 * @param model the model from which this dialog will be filled.
	 * @throws SignalMLException TODO when it is thrown
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		getChooseFilesForMonitorRecordingPanel().fillPanelFromModel(model);
		getChooseVideoForMonitorRecordingPanel().fillPanelFromModel(model);

		MonitorRecordingDescriptor monitorRecordingDescriptor = ((ExperimentDescriptor) model).getMonitorRecordingDescriptor();
		getSaveImpedanceCheckBox().setSelected(monitorRecordingDescriptor.isSaveImpedanceEnabled());
		getAppendTimestampsCheckBox().setSelected(monitorRecordingDescriptor.isAppendTimestampsEnabled());
	}

	/**
	 * Fills the model with the data from this dialog (user input).
	 * @param model the model to be filled
	 * @throws SignalMLException TODO when it is thrown
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		getChooseFilesForMonitorRecordingPanel().fillModelFromPanel(model);
		getChooseVideoForMonitorRecordingPanel().fillModelFromPanel(model);

		MonitorRecordingDescriptor monitorRecordingDescriptor = ((ExperimentDescriptor) model).getMonitorRecordingDescriptor();
		monitorRecordingDescriptor.setSaveImpedanceEnabled(getSaveImpedanceCheckBox().isSelected());
		monitorRecordingDescriptor.setAppendTimestampsEnabled(getAppendTimestampsCheckBox().isSelected());
	}

	/**
	 * Returns the {@link ChooseFilesForMonitorRecordingPanel} used to select
	 * files to which the monitor signal and tags will be recorded.
	 * @return the panel used for file selection
	 */
	public ChooseFilesForMonitorRecordingPanel getChooseFilesForMonitorRecordingPanel() {
		if (chooseFilesForMonitorRecordingPanel == null) {
			chooseFilesForMonitorRecordingPanel = new ChooseFilesForMonitorRecordingPanel();
			chooseFilesForMonitorRecordingPanel.getEnableVideoRecordingPanel().attachComponentToToggle(getChooseVideoForMonitorRecordingPanel());
		}
		return chooseFilesForMonitorRecordingPanel;
	}

	/**
	 * Returns the {@link ChooseFilesForMonitorRecordingPanel} used to select
	 * video stream which will be recorded with the signal.
	 * @return the panel used for video selection
	 */
	public ChooseVideoForMonitorRecordingPanel getChooseVideoForMonitorRecordingPanel() {
		if (chooseVideoForMonitorRecordingPanel == null)
			chooseVideoForMonitorRecordingPanel = new ChooseVideoForMonitorRecordingPanel(this);
		return chooseVideoForMonitorRecordingPanel;
	}

	public JCheckBox getSaveImpedanceCheckBox() {
		if (saveImpedanceCheckBox == null)
			saveImpedanceCheckBox = new JCheckBox(_("Save impedance"));
		return saveImpedanceCheckBox;
	}

	public JCheckBox getAppendTimestampsCheckBox() {
		if (appendTimestampsCheckBox == null)
			appendTimestampsCheckBox = new JCheckBox(_("Append timestamps"));
		return appendTimestampsCheckBox;
	}

	/**
	 * Checks if this dialog is properly filled.
	 * @param model the model for this dialog
	 * @param errors the object in which errors are stored
	 * @throws SignalMLException TODO when it is thrown
	 */
	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		fillModelFromDialog(model);
		getChooseFilesForMonitorRecordingPanel().validatePanel(model, errors);
		getChooseVideoForMonitorRecordingPanel().validatePanel(model, errors);
	}

	/**
	 * Called when the dialog is closed with OK button.
	 */
	@Override
	protected void onDialogCloseWithOK() {
		super.onDialogCloseWithOK();
		getChooseFilesForMonitorRecordingPanel().resetFileNames();
	}

}
