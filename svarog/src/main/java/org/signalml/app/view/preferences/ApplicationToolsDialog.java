package org.signalml.app.view.preferences;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JComponent;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.mp5.MP5Executor;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.validation.Errors;

/**
 * Dialog which allows to manage some external tools for Svarog.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ApplicationToolsDialog extends AbstractDialog  {

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
	 * the {@link ToolsConfigPanel panel} which allows to configure some
	 * external tools for Svarog
	 */
	private ToolsConfigPanel toolsConfigPanel;

	/**
	 * the {@link SignalMLOperationMode mode} in which Svarog is operating
	 */
	private SignalMLOperationMode mode;

	/**
	 * Constructor. Sets message source, parent window, if this dialog
	 * blocks top-level windows and the {@link SignalMLOperationMode mode}
	 * in which Svarog is operating.
	 * @param mode the mode in which Svarog is operating
	 * @param f the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ApplicationToolsDialog(SignalMLOperationMode mode, Window f, boolean isModal) {
		super(f, isModal);
		this.mode = mode;
	}

	/**
	 * Initializes this dialog.
	 */
	@Override
	protected void initialize() {

		setTitle(_("MP Configuration"));
		setMinimumSize(new Dimension(745, 470));

		super.initialize();
	}

	/**
	 * Creates the interface of this dialog - the {@link ToolsConfigPanel panel}
	 * which allows to configure some external tools for Svarog.
	 */
	@Override
	public JComponent createInterface() {

		toolsConfigPanel = new ToolsConfigPanel(fileChooser, mp5ExecutorManager);
		toolsConfigPanel.setMp5LocalExecutorDialog(getMp5LocalExecutorDialog());

		return toolsConfigPanel;
	}

	/**
	 * Fills the panel using the
	 * given {@link ApplicationConfiguration configuration} of Svarog.
	 *
	 * @param model the configuration of Svarog
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		ApplicationConfiguration config = (ApplicationConfiguration) model;

		toolsConfigPanel.fillPanelFromModel(config);
	}

	/**
	 * In the given {@link ApplicationConfiguration
	 * configuration} of Svarog (model) stores the user input from the dialog.
	 *
	 * @param model the configuration of Svarog
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		ApplicationConfiguration config = (ApplicationConfiguration) model;

		toolsConfigPanel.fillModelFromPanel(config);

		if (config.isSaveConfigOnEveryChange()) {
			try {
				mp5ExecutorManager.writeToPersistence(null);
			} catch (Exception ex) {
				logger.error("Failed to save MP executor configuration", ex);
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
		toolsConfigPanel.validatePanel(errors);
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
