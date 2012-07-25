/* ExportSignalDialog.java created 2008-01-27
 *
 */

package org.signalml.app.view.signal.export;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.common.dialogs.AbstractSignalSpaceAwarePresetDialog;
import org.signalml.app.view.signal.signalselection.SignalSpacePanel;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.validation.Errors;

/**
 * Dialog to export the selected part of the signal in the selected format.
 * Contains two panels:
 * <ul>
 * <li>the {@link #getSignalSpacePanel() panel} to select parameters of the
 * signal, such as the time interval, level of processing and selected
 * channels,</li>
 * <li>the {@link RawExportOptionsPanel panel} to select the format in
 * which the signal will be stored.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportSignalDialog extends AbstractSignalSpaceAwarePresetDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SignalSpacePanel panel} to select parameters of the signal,
	 * such as the time interval, level of processing and selected channels
	 */
	private SignalSpacePanel signalSpacePanel;

	/**
	 * the {@link RawExportOptionsPanel panel} to select the format in
	 * which the signal will be stored
	 */
	private ExportFormatPanel formatPanel;

	/**
	 * Constructor. Sets message source, {@link PresetManager preset
	 * manager}, parent window and if this dialog blocks top-level windows.
	 * @param presetManager the preset manager to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ExportSignalDialog(Window w, boolean isModal) {
		super(SvarogApplication.getManagerOfPresetsManagers().getSignalExportPresetManager(), w, isModal);
	}

	/**
	 * Constructor. Sets message source and the {@link PresetManager preset
	 * manager}.
	 * @param presetManager the preset manager to set
	 */
	public ExportSignalDialog(PresetManager presetManager) {
		super(presetManager);
	}

	@Override
	protected void initialize() {
		setTitle(_("Export signal"));
		setResizable(false);
		super.initialize();
	}

	/**
	 * Creates the interface with two sub-panels:
	 * <ul>
	 * <li>the {@link #getSignalSpacePanel() panel} to select parameters of the
	 * signal, such as the time interval, level of processing and selected
	 * channels,</li>
	 * <li>the {@link RawExportOptionsPanel panel} to select the format in
	 * which the signal will be stored.</li>
	 * </ul>
	 */
	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getSignalSpacePanel(), BorderLayout.CENTER);
		interfacePanel.add(getFormatPanel(), BorderLayout.SOUTH);

		return interfacePanel;

	}

	/**
	 * Returns the {@link SignalSpacePanel panel} which allows to select
	 * parameters of the signal, such as the time interval, level of.
	 * processing and selected channels.
	 * If the panel doesn't exist it is created.
	 * @return the panel which allows to select parameters of the signal
	 */
	public SignalSpacePanel getSignalSpacePanel() {
		if (signalSpacePanel == null) {
			signalSpacePanel = new SignalSpacePanel();
		}
		return signalSpacePanel;
	}

	/**
	 * Returns the {@link ExportFormatPanel panel} to select the format
	 * in which the signal will be stored
	 */
	public ExportFormatPanel getFormatPanel() {
		if (formatPanel == null) {
			formatPanel = new ExportFormatPanel();
		}
		return formatPanel;
	}

	/**
	 * {@link #fillDialogFromModel(Object, boolean) Fills} the fields of this
	 * dialog from the given model. {@link SignalSpace} from the model will
	 * be used.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		fillDialogFromModel(model, true);
	}

	/**
	 * Fills the panels of this dialog using the given
	 * {@link SignalExportDescriptor model}:
	 * <ul>
	 * <li>If the {@link SignalSpace signal space} is included in this dialog
	 * - the {@link SignalSpacePanel#fillPanelFromModel(SignalSpace) signal
	 * space panel}<li>
	 * <li>the {@link RawExportOptionsPanel#fillPanelFromModel(
	 * SignalExportDescriptor) signal options panel}</li></ul>
	 * @param model the model to be used
	 * @param includeSpace if the signal space panel should be filled
	 * @throws SignalMLException TODO never thrown
	 */
	public void fillDialogFromModel(Object model, boolean includeSpace) throws SignalMLException {

		SignalExportDescriptor descriptor = (SignalExportDescriptor) model;

		if (includeSpace) {
			getSignalSpacePanel().fillPanelFromModel(descriptor.getSignalSpace());
		}

		getFormatPanel().fillPanelFromModel(descriptor);

	}

	/**
	 * Fills the {@link SignalExportDescriptor model} from panels in this dialog:
	 * <ul>
	 * <li>from {@link SignalSpacePanel#fillModelFromPanel(SignalSpace) signal
	 * space panel},</li>
	 * <li>from {@link RawExportOptionsPanel#fillModelFromPanel(
	 * SignalExportDescriptor) options panel}.</li></ul>
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		SignalExportDescriptor descriptor = (SignalExportDescriptor) model;

		getSignalSpacePanel().fillModelFromPanel(descriptor.getSignalSpace());
		getFormatPanel().fillModelFromPanel(descriptor);

	}

	/**
	 * Validates this dialog.
	 * This dialog is valid if sub-panels ({@link
	 * SignalSpacePanel#validatePanel(Errors) signal space} and
	 * {@link RawExportOptionsPanel#validatePanel(Errors) options}) are
	 * valid.
	 */
	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		getSignalSpacePanel().validatePanel(errors);
		getFormatPanel().validatePanel(errors);

	}


	/**
	 * Creates a {@link SignalExportDescriptor}, {@link #fillModelFromDialog(
	 * Object) fills} it from this dialog and returns it as a {@link Preset}.
	 */
	@Override
	public Preset getPreset() throws SignalMLException {

		SignalExportDescriptor descriptor = new SignalExportDescriptor();

		fillModelFromDialog(descriptor);

		return descriptor;

	}

	/**
	 * {@link #fillDialogFromModel(Object, boolean) Fills} this dialog with
	 * the information from the given {@link Preset} which has to have type
	 * {@link SignalExportDescriptor}.
	 */
	@Override
	public void setPreset(Preset preset, boolean includeSpace) throws SignalMLException {

		SignalExportDescriptor descriptor = (SignalExportDescriptor) preset;
		fillDialogFromModel(descriptor, includeSpace);

	}

	/**
	 * {@link SignalSpacePanel#setConstraints(SignalSpaceConstraints) Sets}
	 * the {@link SignalSpaceConstraints constraints} in the
	 * {@link SignalSpacePanel}.
	 * @param constraints the contstraints to be set
	 */
	public void setConstraints(SignalSpaceConstraints constraints) {

		getSignalSpacePanel().setConstraints(constraints);

	}

	/**
	 * Tries to save current {@link Preset} as the default preset.
	 */
	@Override
	protected void onDialogClose() {
		try {
			Preset preset = getPreset();
			getPresetManager().setDefaultPreset(preset);
		} catch (SignalMLException ex) {
			logger.debug("Failed to get preset", ex);
		}
	}

	/**
	 * The model for this dialog has to have type {@link SignalExportDescriptor}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalExportDescriptor.class.isAssignableFrom(clazz);
	}

}
