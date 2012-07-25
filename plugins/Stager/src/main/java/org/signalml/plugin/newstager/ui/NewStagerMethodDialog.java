/* StagerMethodDialog.java created 2008-02-08
 *
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

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
import javax.swing.WindowConstants;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.method.InputSignalPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.SourceMontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.montage.SourceMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.view.AbstractPluginPresetDialog;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerApplicationData;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.helper.NewStagerConfigurationDefaultsHelper;
import org.springframework.core.io.ClassPathResource;

/**
 * StagerMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o. (dialog design based on work by Hubert Klekowicz)
 */
public class NewStagerMethodDialog extends AbstractPluginPresetDialog {

	private static final long serialVersionUID = 1L;

	public static final String HELP_RULES = "org/signalml/help/stager.html#rules";
	public static final String HELP_DELTA_MIN_AMPLITUDE = "org/signalml/help/stager.html#deltaMinAmplitude";
	public static final String HELP_ALPHA_MIN_AMPLITUDE = "org/signalml/help/stager.html#alphaMinAmplitude";
	public static final String HELP_SPINDLE_MIN_AMPLITUDE = "org/signalml/help/stager.html#spindleMinAmplitude";
	public static final String HELP_PRIMARY_HYPNOGRAM = "org/signalml/help/stager.html#primaryHypnogram";

	public static final String HELP_PARAMETERS = "org/signalml/help/stager.html#parameters";

	public static final String HELP_EMG_TONE_THRESHOLD = "org/signalml/help/stager.html#emgToneThreshold";
	public static final String HELP_MT_EEG_THRESHOLD = "org/signalml/help/stager.html#mtEegThreshold";
	public static final String HELP_MT_EMG_THRESHOLD = "org/signalml/help/stager.html#mtEmgThreshold";
	public static final String HELP_MT_TONE_EMG_THRESHOLD = "org/signalml/help/stager.html#mtToneEmgThreshold";
	public static final String HELP_REM_EOG_DEFLECTION_THRESHOLD = "org/signalml/help/stager.html#remEogDeflectionThreshold";
	public static final String HELP_SEM_EOG_DEFLECTION_THRESHOLD = "org/signalml/help/stager.html#semEogDeflectionThreshold";

	private URL contextHelpURL = null;

	private InputSignalPanel signalPanel;

	private JTabbedPane tabbedPane;
	private NewStagerBasicConfigPanel basicConfigPanel;
	private NewStagerAdvancedConfigPanel advancedConfigPanel;
	private NewStagerThresholdConfigPanel thresholdConfigPanel;

	SourceMontageDialog montageDialog;

	SourceMontage currentMontage;

	public NewStagerMethodDialog(PresetManager presetManager, Window w) {
		super(presetManager, w, true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
		setTitle(_("Stager configuration"));
		setIconImage(IconUtils.loadClassPathImage(NewStagerPlugin.iconPath));
		setResizable(false);
		super.initialize();
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource(
									  "org/signalml/help/stager.html")).getURL();
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		signalPanel = new InputSignalPanel();

		signalPanel.getMontageButton().setAction(new EditMontageAction());

		interfacePanel.add(signalPanel, BorderLayout.NORTH);
		interfacePanel.add(getTabbedPane(), BorderLayout.CENTER);

		getBasicConfigPanel().getEnableAdvancedConfigPanel()
		.getEnableAdvancedCheckBox()
		.addItemListener(new ItemListener() {

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

	public NewStagerBasicConfigPanel getBasicConfigPanel() {
		if (basicConfigPanel == null) {
			basicConfigPanel = new NewStagerBasicConfigPanel(getFileChooser(),
					this);
		}
		return basicConfigPanel;
	}

	public NewStagerAdvancedConfigPanel getAdvancedConfigPanel() {
		if (advancedConfigPanel == null) {
			advancedConfigPanel = new NewStagerAdvancedConfigPanel(this);
		}
		return advancedConfigPanel;
	}

	public NewStagerThresholdConfigPanel getThresholdConfigPanel() {
		if (thresholdConfigPanel == null) {
			thresholdConfigPanel = new NewStagerThresholdConfigPanel(this);
		}
		return thresholdConfigPanel;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP,
										 JTabbedPane.WRAP_TAB_LAYOUT);

			tabbedPane.addTab(_("Basic config"), getBasicConfigPanel());
			tabbedPane.addTab(_("Advanced config"), getAdvancedConfigPanel());
			tabbedPane.addTab(_("Threshold config"), getThresholdConfigPanel());

			tabbedPane.setEnabledAt(1, false);
			tabbedPane.setEnabledAt(2, false);

		}
		return tabbedPane;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		NewStagerApplicationData data = (NewStagerApplicationData) model;

		ExportedSignalDocument signalDocument = data.getSignalDocument();
		String path = "?";
		if (signalDocument instanceof FileBackedDocument) {
			path = ((FileBackedDocument) signalDocument).getBackingFile()
				   .getAbsolutePath();
		}
		signalPanel.getSignalTextField().setText(path);

		// XXX FIXME
		if (path == null || path.compareTo("?") == 0) {
			Dialogs.showExceptionDialog(this, new SignalMLException(
											_("No active signal. Choose a signal tab first.")));
			return;
		} else {
			// XXX FIXME bad place to setting this up here
			//TODO!
			//data.getParameters().setSignalPath(path);
		}

		Preset preset = getPresetManager().getDefaultPreset();
		if (preset != null) {
			setPreset(preset);
		} else {
			fillDialogFromParameters(data.getParameters());
		}

		currentMontage = new Montage(data.getMontage());

	}

	private void fillDialogFromParameters(NewStagerParameters parameters) {
		getBasicConfigPanel().fillPanelFromParameters(parameters);
		getAdvancedConfigPanel().fillPanelFromParameters(parameters);
		getThresholdConfigPanel().fillPanelFromParameters(parameters);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		NewStagerApplicationData data = (NewStagerApplicationData) model;
		data.setMontage(currentMontage);

		fillParametersFromDialog(data.getParameters());

		data.calculate();

	}

	private void fillParametersFromDialog(NewStagerParameters parameters) {
		getBasicConfigPanel().fillParametersFromPanel(parameters);
		getAdvancedConfigPanel().fillParametersFromPanel(parameters);
		getThresholdConfigPanel().fillParametersFromPanel(parameters);
	}

	@Override
	public Preset getPreset() {
		NewStagerParameters parameters = new NewStagerParameters();
		fillParametersFromDialog(parameters);
		return parameters;
	}

	@Override
	public void setPreset(Preset preset) {
		NewStagerParameters parameters = (NewStagerParameters) preset;
		fillDialogFromParameters(parameters);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) {
		// errors.pushNestedPath("parameters"); //TODO
		getBasicConfigPanel().validatePanel(errors);
		getAdvancedConfigPanel().validatePanel(errors);
		getThresholdConfigPanel().validatePanel(errors);
		// errors.popNestedPath();
	}

	@Override
	protected void onDialogClose() {
		Preset preset = getPreset();
		getPresetManager().setDefaultPreset(preset);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewStagerApplicationData.class.isAssignableFrom(clazz);
	}

	protected class EditMontageAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditMontageAction() {
			super(_("Edit montage"));
			putValue(
				AbstractAction.SMALL_ICON,
				IconUtils
				.loadClassPathIcon("org/signalml/app/icon/montage.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
					 _("Edit channel labels and functions"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montageDialog == null) {
				montageDialog = new SourceMontageDialog(
					NewStagerMethodDialog.this, true);
			}

			SourceMontageDescriptor descriptor = new SourceMontageDescriptor(
				currentMontage);

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
			super(_("Restore defaults"));
			putValue(
				AbstractAction.SMALL_ICON,
				IconUtils
				.loadClassPathIcon("org/signalml/app/icon/restoredefaults.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
					 _("Restore default method configuration"));
		}

		public void actionPerformed(ActionEvent ev) {
			NewStagerParameters parameters = (NewStagerParameters) getPreset();
			NewStagerConfigurationDefaultsHelper.GetSharedInstance().setDefaults(parameters);
			setPreset(parameters);
		}

	}
}
