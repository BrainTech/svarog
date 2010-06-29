/* StagerMethodDialog.java created 2008-02-08
 *
 */

package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.signalml.app.config.ConfigurationDefaults;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.InputSignalPanel;
import org.signalml.app.model.SourceMontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.montage.SourceMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.exception.SignalMLException;
import org.signalml.method.stager.StagerParameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Errors;

/** StagerMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 		(dialog design based on work by Hubert Klekowicz)
 */
public class StagerMethodDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	public static final String HELP_RULES	 						= "org/signalml/help/stager.html#rules";
	public static final String HELP_DELTA_MIN_AMPLITUDE				= "org/signalml/help/stager.html#deltaMinAmplitude";
	public static final String HELP_ALPHA_MIN_AMPLITUDE				= "org/signalml/help/stager.html#alphaMinAmplitude";
	public static final String HELP_SPINDLE_MIN_AMPLITUDE			= "org/signalml/help/stager.html#spindleMinAmplitude";
	public static final String HELP_PRIMARY_HYPNOGRAM				= "org/signalml/help/stager.html#primaryHypnogram";

	public static final String HELP_PARAMETERS 						= "org/signalml/help/stager.html#parameters";

	public static final String HELP_EMG_TONE_THRESHOLD				= "org/signalml/help/stager.html#emgToneThreshold";
	public static final String HELP_MT_EEG_THRESHOLD				= "org/signalml/help/stager.html#mtEegThreshold";
	public static final String HELP_MT_EMG_THRESHOLD				= "org/signalml/help/stager.html#mtEmgThreshold";
	public static final String HELP_MT_TONE_EMG_THRESHOLD			= "org/signalml/help/stager.html#mtToneEmgThreshold";
	public static final String HELP_REM_EOG_DEFLECTION_THRESHOLD 	= "org/signalml/help/stager.html#remEogDeflectionThreshold";
	public static final String HELP_SEM_EOG_DEFLECTION_THRESHOLD 	= "org/signalml/help/stager.html#semEogDeflectionThreshold";

	private URL contextHelpURL = null;

	private InputSignalPanel signalPanel;

	private JTabbedPane tabbedPane;
	private StagerBasicConfigPanel basicConfigPanel;
	private StagerAdvancedConfigPanel advancedConfigPanel;
	private StagerThresholdConfigPanel thresholdConfigPanel;

	SourceMontageDialog montageDialog;

	SourceMontage currentMontage;

	public StagerMethodDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w) {
		super(messageSource, presetManager, w, true);
	}

	@Override
	protected JPanel createButtonPane() {
		JPanel buttonPane = super.createButtonPane();
		buttonPane.add(Box.createHorizontalStrut(10), 1);
		buttonPane.add(new JButton(new RestoreDefaultsAction()), 1);
		return buttonPane;
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("stagerMethod.dialog.title"));
		setIconImage(IconUtils.loadClassPathImage(StagerMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource("org/signalml/help/stager.html")).getURL();
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		signalPanel = new InputSignalPanel(messageSource);

		signalPanel.getMontageButton().setAction(new EditMontageAction());

		interfacePanel.add(signalPanel, BorderLayout.NORTH);
		interfacePanel.add(getTabbedPane(), BorderLayout.CENTER);

		getBasicConfigPanel().getEnableAdvancedConfigPanel().getEnableAdvancedCheckBox().addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

				JTabbedPane pane = getTabbedPane();
				if (!selected) {
					if (pane.getSelectedIndex() != 0) {
						pane.setSelectedIndex(0);
					}
				}
				pane.setEnabledAt(1, selected);
				pane.setEnabledAt(2, selected);

			}

		});

		return interfacePanel;

	}

	public StagerBasicConfigPanel getBasicConfigPanel() {
		if (basicConfigPanel == null) {
			basicConfigPanel = new StagerBasicConfigPanel(messageSource, getFileChooser(), this);
		}
		return basicConfigPanel;
	}

	public StagerAdvancedConfigPanel getAdvancedConfigPanel() {
		if (advancedConfigPanel == null) {
			advancedConfigPanel = new StagerAdvancedConfigPanel(messageSource, this);
		}
		return advancedConfigPanel;
	}

	public StagerThresholdConfigPanel getThresholdConfigPanel() {
		if (thresholdConfigPanel == null) {
			thresholdConfigPanel = new StagerThresholdConfigPanel(messageSource, this);
		}
		return thresholdConfigPanel;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

			tabbedPane.addTab(messageSource.getMessage("stagerMethod.dialog.basicTab"), getBasicConfigPanel());
			tabbedPane.addTab(messageSource.getMessage("stagerMethod.dialog.advancedTab"), getAdvancedConfigPanel());
			tabbedPane.addTab(messageSource.getMessage("stagerMethod.dialog.thresholdTab"), getThresholdConfigPanel());

			tabbedPane.setEnabledAt(1, false);
			tabbedPane.setEnabledAt(2, false);

		}
		return tabbedPane;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		StagerApplicationData data = (StagerApplicationData) model;

		SignalDocument signalDocument = data.getSignalDocument();
		String path = "?";
		if (signalDocument instanceof FileBackedDocument) {
			path = ((FileBackedDocument) signalDocument).getBackingFile().getAbsolutePath();
		}
		signalPanel.getSignalTextField().setText(path);

		// XXX FIXME
		if (path == null || path.compareTo("?") == 0) {
			ErrorsDialog.showImmediateExceptionDialog(this, new SignalMLException(messageSource.getMessage("situation.noActiveSignal")));
			return;
		} else {
			// XXX FIXME bad place to setting this up here
			data.getParameters().setSignalPath(path);
		}



		Preset preset = getPresetManager().getDefaultPreset();
		if (preset != null) {
			setPreset(preset);
		} else {
			fillDialogFromParameters(data.getParameters());
		}

		currentMontage = new Montage(data.getMontage());

	}

	private void fillDialogFromParameters(StagerParameters parameters) {

		getBasicConfigPanel().fillPanelFromParameters(parameters);
		getAdvancedConfigPanel().fillPanelFromParameters(parameters);
		getThresholdConfigPanel().fillPanelFromParameters(parameters);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		StagerApplicationData data = (StagerApplicationData) model;
		data.setMontage(currentMontage);

		fillParametersFromDialog(data.getParameters());

		data.calculate();

	}

	private void fillParametersFromDialog(StagerParameters parameters) {

		getBasicConfigPanel().fillParametersFromPanel(parameters);
		getAdvancedConfigPanel().fillParametersFromPanel(parameters);
		getThresholdConfigPanel().fillParametersFromPanel(parameters);

	}

	@Override
	public Preset getPreset() {

		StagerParameters parameters = new StagerParameters();

		fillParametersFromDialog(parameters);

		return parameters;

	}

	@Override
	public void setPreset(Preset preset) {

		StagerParameters parameters = (StagerParameters) preset;

		fillDialogFromParameters(parameters);

	}

	@Override
	public void validateDialog(Object model, Errors errors) {

		errors.pushNestedPath("parameters");
		getBasicConfigPanel().validatePanel(errors);
		getAdvancedConfigPanel().validatePanel(errors);
		getThresholdConfigPanel().validatePanel(errors);
		errors.popNestedPath();

	}

	@Override
	protected void onDialogClose() {
		Preset preset = getPreset();
		getPresetManager().setDefaultPreset(preset);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return StagerApplicationData.class.isAssignableFrom(clazz);
	}

	protected class EditMontageAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditMontageAction() {
			super(messageSource.getMessage("stagerMethod.dialog.editMontage"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/montage.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("stagerMethod.dialog.editMontageToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montageDialog == null) {
				montageDialog = new SourceMontageDialog(messageSource, StagerMethodDialog.this, true);
			}

			SourceMontageDescriptor descriptor = new SourceMontageDescriptor(currentMontage);

			boolean ok = montageDialog.showDialog(descriptor, true);
			if (!ok) {
				return;
			}

			currentMontage = descriptor.getMontage();

		}

	}

	protected class RestoreDefaultsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RestoreDefaultsAction() {
			super(messageSource.getMessage("stagerMethod.dialog.restoreDefaults"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/restoredefaults.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("stagerMethod.dialog.restoreDefaultsToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			StagerParameters parameters = (StagerParameters) getPreset();

			ConfigurationDefaults.setStagerParameters(parameters);

			setPreset(parameters);

		}

	}

}
