package org.signalml.plugin.bookreporter.ui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.bookreporter.BookReporterPlugin;
import org.signalml.plugin.bookreporter.data.BookReporterData;
import org.signalml.plugin.bookreporter.data.BookReporterParameters;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPluginPresetDialog;
import static org.signalml.plugin.i18n.PluginI18n._;
import org.signalml.plugin.method.helper.PluginPresetManagerFilter;
import org.springframework.core.io.ClassPathResource;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerMethodDialog,
 *     which was based on work by Hubert Klekowicz)
 */
public class BookReporterMethodDialog extends AbstractPluginPresetDialog {

	private static final long serialVersionUID = 1L;

	public static final String HELP_RULES = "org/signalml/help/bookReporter.html#rules";
	public static final String HELP_DELTA_MIN_AMPLITUDE = "org/signalml/help/bookReporter.html#deltaMinAmplitude";
	public static final String HELP_ALPHA_MIN_AMPLITUDE = "org/signalml/help/bookReporter.html#alphaMinAmplitude";
	public static final String HELP_SPINDLE_MIN_AMPLITUDE = "org/signalml/help/bookReporter.html#spindleMinAmplitude";
	public static final String HELP_PRIMARY_HYPNOGRAM = "org/signalml/help/bookReporter.html#primaryHypnogram";

	public static final String HELP_PARAMETERS = "org/signalml/help/bookReporter.html#parameters";

	public static final String HELP_EMG_TONE_THRESHOLD = "org/signalml/help/bookReporter.html#emgToneThreshold";
	public static final String HELP_MT_EEG_THRESHOLD = "org/signalml/help/bookReporter.html#mtEegThreshold";
	public static final String HELP_MT_EMG_THRESHOLD = "org/signalml/help/bookReporter.html#mtEmgThreshold";
	public static final String HELP_MT_TONE_EMG_THRESHOLD = "org/signalml/help/bookReporter.html#mtToneEmgThreshold";
	public static final String HELP_REM_EOG_DEFLECTION_THRESHOLD = "org/signalml/help/bookReporter.html#remEogDeflectionThreshold";
	public static final String HELP_SEM_EOG_DEFLECTION_THRESHOLD = "org/signalml/help/bookReporter.html#semEogDeflectionThreshold";

	private URL contextHelpURL = null;

	private BookReporterBookPanel bookPanel;
	private BookReporterConfigPanel advancedConfigPanel;

	private BookReporterParameters currentParametersPreset;

	private boolean isUpdatingPreset;

	public BookReporterMethodDialog(PresetManager presetManager, Window w) {
		super(new PluginPresetManagerFilter(presetManager,
				BookReporterMethodDialog.GetPresetClasses()), w, true);
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
		setTitle(_("Book reporter configuration"));
		setIconImage(IconUtils.loadClassPathImage(BookReporterPlugin.iconPath));
		setResizable(false);
		super.initialize();
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource(
						"org/signalml/help/bookReporter.html")).getURL();
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getBookPanel(), BorderLayout.NORTH);
		interfacePanel.add(getAdvancedConfigPanel(), BorderLayout.CENTER);

		return interfacePanel;

	}

	public BookReporterConfigPanel getAdvancedConfigPanel() {
		if (advancedConfigPanel == null) {
			advancedConfigPanel = new BookReporterConfigPanel(this);
		}
		return advancedConfigPanel;
	}

	public BookReporterBookPanel getBookPanel() {
		if (bookPanel == null) {
			bookPanel = new BookReporterBookPanel(getFileChooser());
		}
		return bookPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		BookReporterData data = (BookReporterData) model;

		Preset preset = getPresetManager().getDefaultPreset();
		if (preset != null) {
			setPreset(preset);
		} else {
			fillDialogFromParameters(data.getParameters());
		}

	}

	private void fillDialogFromParameters(BookReporterParameters parameters) {
		getBookPanel().fillPanelFromModel(parameters);
		getAdvancedConfigPanel().fillPanelFromParameters(parameters);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		BookReporterData data = (BookReporterData) model;
		fillParametersFromDialog(data.getParameters());
	}

	private void fillParametersFromDialog(BookReporterParameters parameters) {
		getBookPanel().fillModelFromPanel(parameters);
		getAdvancedConfigPanel().fillParametersFromPanel(parameters);
	}

	@Override
	public Preset getPreset() {
		this.updateCurrentPreset();
		BookReporterParameters parameters = new BookReporterParameters();
		fillParametersFromDialog(parameters);
		return parameters;
	}

	@Override
	public void setPreset(Preset preset) {
		BookReporterParameters parameters;

		try {
			parameters = (BookReporterParameters) preset;
		} catch (ClassCastException e) {
			return;
		}

		this.isUpdatingPreset = true;
		try {
			fillDialogFromParameters(parameters);
			this.currentParametersPreset = parameters;
		} finally {
			this.isUpdatingPreset = false;
		}
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) {
		getBookPanel().validatePanel(errors);
		getAdvancedConfigPanel().validatePanel(errors);
	}

	@Override
	protected void onDialogClose() {
		Preset preset = getPreset();
		getPresetManager().setDefaultPreset(preset);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BookReporterData.class.isAssignableFrom(clazz);
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
			BookReporterParameters parameters = new BookReporterParameters();
			setPreset(parameters);
		}

	}

	protected void updateCurrentPreset() {
		if (isUpdatingPreset) {
			return;
		}
		this.isUpdatingPreset = true;
		try {
			BookReporterParameters parameters = this.getParametersPreset();

			getBookPanel().fillModelFromPanel(parameters);
			getAdvancedConfigPanel().fillParametersFromPanel(parameters);

			getBookPanel().fillPanelFromModel(parameters);
			getAdvancedConfigPanel().fillPanelFromParameters(parameters);
		} finally {
			this.isUpdatingPreset = false;
		}
	}

	private BookReporterParameters getParametersPreset() {
		if (this.currentParametersPreset != null) {
			return this.currentParametersPreset;
		}

		BookReporterParameters parametersPreset = null;
		Preset preset = getPresetManager().getDefaultPreset();

		try {
			parametersPreset = (BookReporterParameters) preset;
		} catch (ClassCastException e) {
			//do nothing
		}

		if (parametersPreset == null) {
			parametersPreset = new BookReporterParameters();
		}

		return parametersPreset;
	}

	private static Collection<Class<? extends Preset>> GetPresetClasses() {
		Collection<Class<? extends Preset>> l = new ArrayList<Class<? extends Preset>>();
		l.add(BookReporterParameters.class);
		return l;
	}

}
