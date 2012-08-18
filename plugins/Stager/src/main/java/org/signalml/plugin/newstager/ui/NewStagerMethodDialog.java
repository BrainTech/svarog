/* StagerMethodDialog.java created 2008-02-08
 *
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.method.InputSignalPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.SourceMontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.montage.SourceMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.view.AbstractPluginPresetDialog;
import org.signalml.plugin.method.helper.PluginPresetManagerFilter;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerApplicationData;
import org.signalml.plugin.newstager.data.NewStagerParametersPreset;
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

	private static final int BASIC_PARAMETERS_TAB = 0;
	private static final int ADVANCED_PARAMETERS_TAB = 1;
	private static final int THRESHOLD_TAB = 2;

	private URL contextHelpURL = null;

	private InputSignalPanel signalPanel;

	private JTabbedPane tabbedPane;

	private int currentPane;

	private NewStagerBasicConfigPanel basicConfigPanel;
	private NewStagerAdvancedConfigPanel advancedConfigPanel;
	private NewStagerThresholdConfigPanel thresholdConfigPanel;

	SourceMontageDialog montageDialog;

	SourceMontage currentMontage;

	private NewStagerParametersPreset currentParametersPreset;

	private NewStagerAdvancedConfigObservable advancedConfigObservable;

	private boolean isUpdatingPreset;

	protected boolean advancedParametersEnabled;

	public NewStagerMethodDialog(PresetManager presetManager, Window w) {
		super(new PluginPresetManagerFilter(presetManager,
				NewStagerMethodDialog.GetPresetClasses()), w, true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.isUpdatingPreset = false;
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

		this.advancedConfigObservable = new NewStagerAdvancedConfigObservable();

		interfacePanel.add(signalPanel, BorderLayout.NORTH);
		interfacePanel.add(getTabbedPane(), BorderLayout.CENTER);

		this.advancedConfigObservable.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object arg) {
				boolean enabled = advancedConfigObservable.getEnabled();

				JTabbedPane pane = getTabbedPane();
				if (!enabled) {
					if (pane.getSelectedIndex() != BASIC_PARAMETERS_TAB) {
						pane.setSelectedIndex(BASIC_PARAMETERS_TAB);
					}
				}
				pane.setEnabledAt(ADVANCED_PARAMETERS_TAB, enabled);
				pane.setEnabledAt(THRESHOLD_TAB, enabled);

				advancedParametersEnabled = enabled;
			}
		});

		return interfacePanel;

	}

	public NewStagerBasicConfigPanel getBasicConfigPanel() {
		if (basicConfigPanel == null) {
			basicConfigPanel = new NewStagerBasicConfigPanel(getFileChooser(),
					this, this.advancedConfigObservable);
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

			tabbedPane.setEnabledAt(ADVANCED_PARAMETERS_TAB, false);
			tabbedPane.setEnabledAt(THRESHOLD_TAB, false);

			this.currentPane = tabbedPane.getModel().getSelectedIndex();
			this.advancedParametersEnabled = false;

			tabbedPane.getModel().addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					updateCurrentPreset();
					currentPane = tabbedPane.getModel().getSelectedIndex();
				}
			});
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
			// TODO!
			// data.getParameters().setSignalPath(path);
		}

		Preset preset = getPresetManager().getDefaultPreset();
		if (preset != null) {
			setPreset(preset);
		} else {
			fillDialogFromParameters(new NewStagerParametersPreset(
					data.getParameters(), false, true, true, true));
		}

		currentMontage = new Montage(data.getMontage());

	}

	private void fillDialogFromParameters(
			NewStagerParametersPreset parametersPreset) {
		this.advancedParametersEnabled = parametersPreset.enableAdvancedParameters;

		getBasicConfigPanel().fillPanelFromParameters(parametersPreset);
		getAdvancedConfigPanel().fillPanelFromParameters(parametersPreset);
		getThresholdConfigPanel().fillPanelFromParameters(parametersPreset);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		NewStagerApplicationData data = (NewStagerApplicationData) model;
		data.setMontage(currentMontage);

		fillParametersFromDialog(new NewStagerParametersPreset(
				data.getParameters(), false, true, true, true));

		data.calculate();
	}

	private void fillParametersFromDialog(
			NewStagerParametersPreset parametersPreset) {
		NewStagerBasicConfigPanel basicPanel = getBasicConfigPanel();
		NewStagerAdvancedConfigPanel advancedPanel = getAdvancedConfigPanel();

		if (this.advancedParametersEnabled) {
			/*
			if (this.currentParametersPreset != null) {
				this.fillDialogFromParameters(this.getParametersPreset());
			}*/

			switch (this.getTabbedPane().getSelectedIndex()) {
			case BASIC_PARAMETERS_TAB:
				advancedPanel.fillParametersFromPanel(parametersPreset);
				basicPanel.fillParametersFromPanel(parametersPreset);
			default:
				basicPanel.fillParametersFromPanel(parametersPreset);
				advancedPanel.fillParametersFromPanel(parametersPreset);
			}
		} else {
			basicPanel.fillParametersFromPanel(parametersPreset);
		}
		getThresholdConfigPanel().fillParametersFromPanel(parametersPreset);
	}

	@Override
	public Preset getPreset() {
		this.updateCurrentPreset();
		NewStagerParametersPreset parametersPreset = new NewStagerParametersPreset();
		fillParametersFromDialog(parametersPreset);
		return parametersPreset;
	}

	@Override
	public void setPreset(Preset preset) {
		NewStagerParametersPreset parametersPreset;

		try {
			parametersPreset = (NewStagerParametersPreset) preset;
		} catch (ClassCastException e) {
			return;
		}

		this.isUpdatingPreset = true;
		try {
			fillDialogFromParameters(parametersPreset);
			this.currentParametersPreset = parametersPreset;
		} finally {
			this.isUpdatingPreset = false;
		}
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
			this.setEnabled(false);
			putValue(
					AbstractAction.SMALL_ICON,
					IconUtils
							.loadClassPathIcon("org/signalml/app/icon/montage.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
					_("Edit channel labels and functions"));
		}

		@Override
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

		@Override
		public void actionPerformed(ActionEvent ev) {
			NewStagerParametersPreset parametersPreset = new NewStagerParametersPreset();
			NewStagerConfigurationDefaultsHelper.GetSharedInstance()
					.setDefaults(parametersPreset.parameters);
			parametersPreset.enableAdvancedParameters = false;
			parametersPreset.isAutoAlphaAmplitude = false;
			parametersPreset.isAutoDeltaAmplitude = false;
			parametersPreset.isAutoSpindleAmplitude = false;
			setPreset(parametersPreset);
		}

	}

	protected void updateCurrentPreset() {
		if (isUpdatingPreset) {
			return;
		}
		this.isUpdatingPreset = true;
		try {

			NewStagerParametersPreset parametersPreset = this.getParametersPreset();

			switch (this.currentPane) {
			case BASIC_PARAMETERS_TAB:
				getBasicConfigPanel().fillParametersFromPanel(parametersPreset);
				break;
			case ADVANCED_PARAMETERS_TAB:
				getAdvancedConfigPanel().fillParametersFromPanel(parametersPreset);
				break;
			case THRESHOLD_TAB:
				getThresholdConfigPanel().fillParametersFromPanel(parametersPreset);
				break;
			default:
				return;
			}

			switch (this.tabbedPane.getModel().getSelectedIndex()) {
			case BASIC_PARAMETERS_TAB:
				getBasicConfigPanel().fillPanelFromParameters(parametersPreset);
				break;
			case ADVANCED_PARAMETERS_TAB:
				getAdvancedConfigPanel().fillPanelFromParameters(parametersPreset);
				break;
			case THRESHOLD_TAB:
				getThresholdConfigPanel().fillPanelFromParameters(parametersPreset);
				break;
			default:
				return;
			}
		} finally {
			this.isUpdatingPreset = false;
		}
	}

	private NewStagerParametersPreset getParametersPreset() {
		if (this.currentParametersPreset != null) {
			return this.currentParametersPreset;
		}

		NewStagerParametersPreset parametersPreset;
		Preset preset = getPresetManager().getDefaultPreset();

		try {
			parametersPreset = (NewStagerParametersPreset) preset;
		} catch (ClassCastException e) {
			parametersPreset = new NewStagerParametersPreset();
		}

		return parametersPreset;
	}

	private static Collection<Class<? extends Preset>> GetPresetClasses() {
		Collection<Class<? extends Preset>> l = new ArrayList<Class<? extends Preset>>();
		l.add(NewStagerParametersPreset.class);
		return l;
	}

}
