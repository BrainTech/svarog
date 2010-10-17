/* ExportSignalDialog.java created 2008-01-27
 *
 */

package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.view.element.ExportSignalOptionsPanel;
import org.signalml.app.view.element.SignalSpacePanel;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ExportSignalDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportSignalDialog extends AbstractSignalSpaceAwarePresetDialog {

	private static final long serialVersionUID = 1L;

	private SignalSpacePanel signalSpacePanel;
	private ExportSignalOptionsPanel optionsPanel;

	public ExportSignalDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	public ExportSignalDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("exportSignalDialog.title"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getSignalSpacePanel(), BorderLayout.CENTER);
		interfacePanel.add(getOptionsPanel(), BorderLayout.SOUTH);

		return interfacePanel;

	}

	public SignalSpacePanel getSignalSpacePanel() {
		if (signalSpacePanel == null) {
			signalSpacePanel = new SignalSpacePanel(messageSource);
		}
		return signalSpacePanel;
	}

	public ExportSignalOptionsPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new ExportSignalOptionsPanel(messageSource);
		}
		return optionsPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		fillDialogFromModel(model, true);
	}

	public void fillDialogFromModel(Object model, boolean includeSpace) throws SignalMLException {

		SignalExportDescriptor descriptor = (SignalExportDescriptor) model;

		if (includeSpace) {
			getSignalSpacePanel().fillPanelFromModel(descriptor.getSignalSpace());
		}

		getOptionsPanel().fillPanelFromModel(descriptor);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		SignalExportDescriptor descriptor = (SignalExportDescriptor) model;

		getSignalSpacePanel().fillModelFromPanel(descriptor.getSignalSpace());
		getOptionsPanel().fillModelFromPanel(descriptor);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		getSignalSpacePanel().validatePanel(errors);
		getOptionsPanel().validatePanel(errors);

	}

	@Override
	public Preset getPreset() throws SignalMLException {

		SignalExportDescriptor descriptor = new SignalExportDescriptor();

		fillModelFromDialog(descriptor);

		return descriptor;

	}

	@Override
	public void setPreset(Preset preset, boolean includeSpace) throws SignalMLException {

		SignalExportDescriptor descriptor = (SignalExportDescriptor) preset;

		fillDialogFromModel(descriptor, includeSpace);

	}

	public void setConstraints(SignalSpaceConstraints constraints) {

		getSignalSpacePanel().setConstraints(constraints);

	}

	@Override
	protected void onDialogClose() {
		try {
			Preset preset = getPreset();
			getPresetManager().setDefaultPreset(preset);
		} catch (SignalMLException ex) {
			logger.debug("Failed to get preset", ex);
		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalExportDescriptor.class.isAssignableFrom(clazz);
	}

}
