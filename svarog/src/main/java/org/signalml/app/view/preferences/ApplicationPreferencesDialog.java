/* ApplicationPreferencesDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.preferences;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.action.document.RegisterCodecAction;
import org.signalml.app.action.signal.RemoveCodecAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.signal.SignalMLCodecListModel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.app.view.signal.signalml.RegisterCodecDialog;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.method.mp5.MP5Executor;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Tag;
import org.springframework.validation.Errors;

/**
 * Dialog which allows to manage the options of Svarog.
 * Contains a tabbed pane with tabs to manage different types of options:
 * <ul>
 * <li>the {@link SignalViewingConfigPanel panel} which allows to select how
 * the signal should be displayed by default,</li>
 * <li>the {@link TaggingConfigPanel panel} with options for {@link Tag
 * tags},</li>
 * <li>the {@link MiscellaneousConfigPanel panel} with various "other"
 * options,</li>
 * <li>the {@link SignalZoomSettingsPanel panel} which allows to select
 * how the zoomed signal should be displayed,</li></ul>
 * and if Svarog is running in {@link SignalMLOperationMode#APPLICATION
 * APPLICATION mode}:<ul>
 * <li>the {@link CodecManagerConfigPanel panel} which allows the management
 * of {@link SignalMLCodec codecs},</li>
 * <li>the {@link ToolsConfigPanel panel} which allows to configure some
 * external tools for Svarog.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplicationPreferencesDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link ViewerFileChooser chooser} of files
	 */
	private ViewerFileChooser fileChooser;
	/**
	 * the {@link MP5ExecutorManager manager} of MP5 {@link MP5Executor
	 * executors}
	 */
	private MP5ExecutorManager mp5ExecutorManager;
	/**
	 * the {@link MP5LocalExecutorDialog dialog} to select the local
	 * {@link MP5Executor executor} for MP5
	 */
	private MP5LocalExecutorDialog mp5LocalExecutorDialog;

	/**
	 * the {@link CodecManagerConfigPanel panel} which allows the management
	 * of {@link SignalMLCodec codecs}
	 */
	private CodecManagerConfigPanel codecManagerPanel;
	/**
	 * the {@link SignalViewingConfigPanel panel} which allows to select how
	 * the signal should be displayed by default
	 */
	private SignalViewingConfigPanel signalViewingConfigPanel;
	/**
	 * the {@link MonitorConfigPanel} which allows to configurate
	 * signal recording options
	 */
	private MonitorConfigPanel monitorConfigPanel;
	/**
	 * the {@link TaggingConfigPanel panel} with options for {@link Tag tags}
	 */
	private TaggingConfigPanel taggingConfigPanel;
	/**
	 * the {@link MiscellaneousConfigPanel panel} with various "other" options
	 */
	private MiscellaneousConfigPanel miscellaneousConfigPanel;
	/**
	 * the {@link SignalZoomSettingsPanel panel} which allows to select how the
	 * zoomed signal should be displayed
	 */
	private SignalZoomSettingsPanel signalZoomSettingsPanel;

	/**
	 * the {@link ToolsConfigPanel panel} which allows to configure some
	 * external tools for Svarog
	 */
	private ToolsConfigPanel toolsConfigPanel;

	/**
	 * the {@link RegisterCodecDialog dialog} to register a new
	 * {@link SignalMLCodec codec}
	 */
	private RegisterCodecDialog registerCodecDialog;
	/**
	 * the {@link SignalMLCodecManager manager} of {@link SignalMLCodec codecs}
	 */
	private SignalMLCodecManager codecManager;
	/**
	 * the {@link PleaseWaitDialog dialog} shown when the user has to wait
	 */
	private PleaseWaitDialog pleaseWaitDialog;

	/**
	 * the {@link RegisterCodecAction action} which registers a
	 * {@link SignalMLCodec codec}
	 */
	private RegisterCodecAction registerCodecAction;
	/**
	 * the {@link RemoveCodecAction action} which removes a
	 * {@link SignalMLCodec codec}
	 */
	private RemoveCodecAction removeCodecAction;

	/**
	 * the {@link SignalMLOperationMode mode} in which Svarog is operating
	 */
	private SignalMLOperationMode mode;
	/**
	 * the profile directory
	 */
	private File profileDir;

	/**
	 * Constructor. Sets message source, parent window, if this dialog
	 * blocks top-level windows and the {@link SignalMLOperationMode mode}
	 * in which Svarog is operating.
	 * @param mode the mode in which Svarog is operating
	 * @param f the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ApplicationPreferencesDialog(SignalMLOperationMode mode, Window f, boolean isModal) {
		super(f, isModal);
		this.mode = mode;
	}

	/**
	 * Initializes this dialog:
	 * <ul>
	 * <li>Calls the initialization in the parent class.</li>
	 * <li>Creates the action to {@link RegisterCodecAction register} and
	 * {@link RemoveCodecAction remove} a codec and sets both actions in the
	 * {@link CodecManagerConfigPanel codec manger panel}.</li>
	 * <li>Creates the {@link SignalMLCodecListModel model} for the list of
	 * {@link SignalMLCodec codecs}, the {@link SignalMLCodecSelector selector}
	 * for it and sets it in the codec manger panel.</li></ul>
	 */
	@Override
	protected void initialize() {

		setTitle(_("Preferences"));
		setMinimumSize(new Dimension(745, 470));

		super.initialize();

		if (mode == SignalMLOperationMode.APPLICATION) {

			SignalMLCodecListModel codecListModel = new SignalMLCodecListModel();
			codecListModel.setCodecManager(codecManager);

			SignalMLCodecSelector selector = new SignalMLCodecSelector() {

				@Override
				public SignalMLCodec getSelectedCodec() {
					return (SignalMLCodec) codecManagerPanel.getCodecList().getSelectedValue();
				}

				@Override
				public void setSelectedCodec(SignalMLCodec codec) {
					codecManagerPanel.getCodecList().setSelectedValue(codec, true);
				}

			};

			registerCodecAction = new RegisterCodecAction();
			registerCodecAction.setRegisterCodecDialog(getRegisterCodecDialog());
			registerCodecAction.setSelector(selector);
			registerCodecAction.setPleaseWaitDialog(getPleaseWaitDialog());
			registerCodecAction.initializeAll();

			removeCodecAction = new RemoveCodecAction();
			removeCodecAction.setCodecManager(codecManager);
			removeCodecAction.setSelector(selector);

			codecManagerPanel.getCodecList().setModel(codecListModel);
			codecManagerPanel.getRegisterCodecButton().setAction(registerCodecAction);
			codecManagerPanel.getRemoveCodecButton().setAction(removeCodecAction);

		}

	}

	/**
	 * Creates the interface of this dialog - the tabbed pane with 5-7 tabs:
	 * <ul>
	 * <li>the {@link SignalViewingConfigPanel panel} which allows to select how
	 * the signal should be displayed by default,</li>
	 * <li>the {@link TaggingConfigPanel panel} with options for {@link Tag
	 * tags},</li>
	     * <li>the {@link MonitorConfigPanel} with options for
	     * signal recording,</li>
	 * <li>the {@link MiscellaneousConfigPanel panel} with various "other"
	 * options,</li>
	 * <li>the {@link SignalZoomSettingsPanel panel} which allows to select
	 * how the zoomed signal should be displayed,</li></ul>
	 * and if Svarog is running in {@link SignalMLOperationMode#APPLICATION
	 * APPLICATION mode}:<ul>
	 * <li>the {@link CodecManagerConfigPanel panel} which allows the management
	 * of {@link SignalMLCodec codecs},</li>
	 * <li>the {@link ToolsConfigPanel panel} which allows to configure some
	 * external tools for Svarog.</li></ul>
	 */
	@Override
	public JComponent createInterface() {

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBorder(new EmptyBorder(3,3,3,3));

		signalViewingConfigPanel = new SignalViewingConfigPanel();
		taggingConfigPanel = new TaggingConfigPanel();
		miscellaneousConfigPanel = new MiscellaneousConfigPanel(mode);
		signalZoomSettingsPanel = new SignalZoomSettingsPanel(false);
		monitorConfigPanel = new MonitorConfigPanel();

		JPanel signalViewingContainPanel = new JPanel(new BorderLayout());
		signalViewingContainPanel.add(signalViewingConfigPanel, BorderLayout.NORTH);
		signalViewingContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel taggingContainPanel = new JPanel(new BorderLayout());
		taggingContainPanel.add(taggingConfigPanel, BorderLayout.NORTH);
		taggingContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel miscellaneousContainPanel = new JPanel(new BorderLayout());
		miscellaneousContainPanel.add(miscellaneousConfigPanel, BorderLayout.NORTH);
		miscellaneousContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel zoomSettingsContainPanel = new JPanel(new BorderLayout());
		zoomSettingsContainPanel.add(signalZoomSettingsPanel, BorderLayout.NORTH);
		zoomSettingsContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel signalRecordingContainPanel = new JPanel(new BorderLayout());
		signalRecordingContainPanel.add(monitorConfigPanel, BorderLayout.NORTH);
		signalRecordingContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		tabbedPane.addTab(_("Signal viewing"), signalViewingContainPanel);
		tabbedPane.addTab(_("Signal zooming"), zoomSettingsContainPanel);
		tabbedPane.addTab(_("Tagging"), taggingContainPanel);
		tabbedPane.addTab(_("Online"), signalRecordingContainPanel);
		tabbedPane.addTab(_("Miscellaneous"), miscellaneousContainPanel);

		if (mode == SignalMLOperationMode.APPLICATION) {

			codecManagerPanel = new CodecManagerConfigPanel();

			toolsConfigPanel = new ToolsConfigPanel(fileChooser, mp5ExecutorManager);
			toolsConfigPanel.setMp5LocalExecutorDialog(getMp5LocalExecutorDialog());

			JPanel codecManagerContainPanel = new JPanel(new BorderLayout());
			codecManagerContainPanel.add(codecManagerPanel, BorderLayout.NORTH);
			codecManagerContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

			JPanel toolsContainPanel = new JPanel(new BorderLayout());
			toolsContainPanel.add(toolsConfigPanel, BorderLayout.NORTH);
			toolsContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

			tabbedPane.insertTab(_("Tools"), null, toolsContainPanel, null, 3);
			tabbedPane.insertTab(_("SignalML Codecs"), null, codecManagerContainPanel, null, 5);

		}

		return tabbedPane;

	}

	/**
	 * Fills the panels (tabs) using the
	 * given {@link ApplicationConfiguration configuration} of Svarog:
	 * <ul>
	 * <li>the {@link SignalViewingConfigPanel#fillPanelFromModel(
	 * ApplicationConfiguration) signal viewing panel},</li>
	     * <li>the {@link SignalViewingConfigPanel#fillPanelFromModel(
	 * ApplicationConfiguration) signal viewing panel},</li>
	 * <li>the {@link MonitorConfigPanel#fillPanelFromModel(
	 * ApplicationConfiguration) tagging configuration panel},</li>
	 * <li>the {@link MiscellaneousConfigPanel#fillPanelFromModel(
	 * ApplicationConfiguration) panel} with "other" options,</li>
	 * <li>the {@link SignalZoomSettingsPanel#fillPanelFromModel(
	 * org.signalml.app.config.ZoomSignalSettings) signal zooming panel},</li>
	 * </ul>
	 * @param model the configuration of Svarog
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		ApplicationConfiguration config = (ApplicationConfiguration) model;

		// note the "save on every change" checkbox has no immediate effect on behaviour of codec manager
		if (mode == SignalMLOperationMode.APPLICATION) {
			removeCodecAction.setApplicationConfig(config);

			toolsConfigPanel.fillPanelFromModel(config);
		}

		signalViewingConfigPanel.fillPanelFromModel(config);
		taggingConfigPanel.fillPanelFromModel(config);
		miscellaneousConfigPanel.fillPanelFromModel(config);
		signalZoomSettingsPanel.fillPanelFromModel(config.getZoomSignalSettings());
		monitorConfigPanel.fillPanelFromModel(config);

	}

	/**
	 * In the given {@link ApplicationConfiguration
	 * configuration} of Svarog (model) stores the user input from:
	 * <ul>
	 * <li>the {@link SignalViewingConfigPanel#fillModelFromPanel(
	 * ApplicationConfiguration) signal viewing panel},</li>
	     * <li>the {@link MonitorConfigPanel#fillModelFromPanel(
	 * ApplicationConfiguration) signal viewing panel},</li>
	 * <li>the {@link TaggingConfigPanel#fillModelFromPanel(
	 * ApplicationConfiguration) tagging configuration panel},</li>
	 * <li>the {@link MiscellaneousConfigPanel#fillModelFromPanel(
	 * ApplicationConfiguration) panel} with "other" options,</li>
	 * <li>the {@link SignalZoomSettingsPanel#fillModelFromPanel(
	 * org.signalml.app.config.ZoomSignalSettings) signal zooming panel},</li>
	 * </ul>
	 * @param model the configuration of Svarog
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		ApplicationConfiguration config = (ApplicationConfiguration) model;

		signalViewingConfigPanel.fillModelFromPanel(config);
		taggingConfigPanel.fillModelFromPanel(config);
		miscellaneousConfigPanel.fillModelFromPanel(config);
		signalZoomSettingsPanel.fillModelFromPanel(config.getZoomSignalSettings());
		monitorConfigPanel.fillModelFromPanel(config);

		config.applySystemSettings();

		if (mode == SignalMLOperationMode.APPLICATION) {

			toolsConfigPanel.fillModelFromPanel(config);

			if (config.isSaveConfigOnEveryChange()) {
				try {
					codecManager.writeToPersistence(null);
				} catch (Exception ex) {
					logger.error("Failed to save codec configuration", ex);
				}
				try {
					mp5ExecutorManager.writeToPersistence(null);
				} catch (Exception ex) {
					logger.error("Failed to save MP executor configuration", ex);
				}
				try {
					config.writeToPersistence(null);
				} catch (Exception ex) {
					logger.error("Failed to save configuration", ex);
				}
			}

		}
	}

	/**
	 * Validates this dialog. This dialog is valid if following panels
	 * are valid:
	 * <ul>
	 * <li>the {@link SignalViewingConfigPanel#validate(Errors) signal viewing
	 * panel},</li>
	 * <li>the {@link TaggingConfigPanel#validate(Errors) tagging configuration
	 * panel},</li>
	 * <li>the {@link MiscellaneousConfigPanel#validate(Errors) panel} with
	 * "other" options,</li>
	 * <li>the {@link SignalZoomSettingsPanel#validate(Errors) signal zooming
	 * panel}.</li>
	 * </ul>
	 */
	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		signalViewingConfigPanel.validate(errors);
		taggingConfigPanel.validate(errors);
		miscellaneousConfigPanel.validate(errors);
		monitorConfigPanel.validate(errors);

		signalZoomSettingsPanel.validate(errors);

		if (mode == SignalMLOperationMode.APPLICATION) {
			toolsConfigPanel.validatePanel(errors);
		}

	}

	/**
	 * The model for this dialog must be of type
	 * {@link ApplicationConfiguration}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ApplicationConfiguration.class.isAssignableFrom(clazz);
	}

	/**
	 * Returns the profile directory.
	 * @return the profile directory
	 */
	public File getProfileDir() {
		return profileDir;
	}

	/**
	 * Sets the profile directory.
	 * @param profileDir the profile directory
	 */
	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	/**
	 * Returns the {@link RegisterCodecDialog dialog} to register a new
	 * {@link SignalMLCodec codec}.
	 * If the dialog doesn't exist it is created.
	 * @return the dialog to register a new codec
	 */
	protected RegisterCodecDialog getRegisterCodecDialog() {
		if (registerCodecDialog == null) {
			registerCodecDialog = new RegisterCodecDialog(this,true);
			registerCodecDialog.setCodecManager(codecManager);
			registerCodecDialog.setProfileDir(profileDir);
		}
		return registerCodecDialog;
	}

	/**
	 * Returns the {@link SignalMLCodecManager manager} of {@link SignalMLCodec
	 * codecs}
	 * @return the manager of codecs
	 */
	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	/**
	 * Sets the {@link SignalMLCodecManager manager} of {@link SignalMLCodec
	 * codecs}
	 * @param codecManager the manager of codecs
	 */
	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	/**
	 * Returns the {@link PleaseWaitDialog dialog} shown when the user has to
	 * wait.
	 * If the dialog doesn't exist it is created.
	 * @return the dialog shown when the user has to wait
	 */
	protected PleaseWaitDialog getPleaseWaitDialog() {
		if (pleaseWaitDialog == null) {
			pleaseWaitDialog = new PleaseWaitDialog(this);
			pleaseWaitDialog.initializeNow();
		}
		return pleaseWaitDialog;
	}

	/**
	 * Returns the {@link ViewerFileChooser chooser} of files.
	 * @return the chooser of files
	 */
	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Sets the {@link ViewerFileChooser chooser} of files.
	 * @param fileChooser the chooser of files
	 */
	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	/**
	 * Returns the {@link MP5ExecutorManager manager} of MP5 {@link MP5Executor
	 * executors}.
	 * @return the manager of MP5 executors
	 */
	public MP5ExecutorManager getMp5ExecutorManager() {
		return mp5ExecutorManager;
	}

	/**
	 * Sets the {@link MP5ExecutorManager manager} of MP5 {@link MP5Executor
	 * executors}.
	 * @param mp5ExecutorManager the manager of MP5 executors
	 */
	public void setMp5ExecutorManager(MP5ExecutorManager mp5ExecutorManager) {
		this.mp5ExecutorManager = mp5ExecutorManager;
	}

	/**
	 * Returns the {@link MP5LocalExecutorDialog dialog} to select the local
	 * {@link MP5Executor executor} for MP5.
	 * If the dialog doesn't exist it is created.
	 * @return the dialog to select the local executor for MP5
	 */
	protected MP5LocalExecutorDialog getMp5LocalExecutorDialog() {
		if (mp5LocalExecutorDialog == null) {
			mp5LocalExecutorDialog = new MP5LocalExecutorDialog(this,true);
			mp5LocalExecutorDialog.setFileChooser(fileChooser);
		}
		return mp5LocalExecutorDialog;
	}

}
