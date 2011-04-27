/* ToolsConfigPanel.java created 2007-12-14
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.method.artifact.ArtifactToolConfigPanel;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.method.mp5.MP5RemoteExecutorDialog;
import org.signalml.app.method.mp5.MP5ToolConfigPanel;
import org.signalml.app.method.stager.StagerToolConfigPanel;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.method.mp5.MP5Executor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * The panel which allows to configure 3 tools for Svarog:
 * <ul>
 * <li>the {@link MP5ToolConfigPanel mp5 tool},</li>
 * <li>the {@link ArtifactToolConfigPanel artifact detection tool},</li>
 * <li>the {@link StagerToolConfigPanel automatic sleep stager tool}.</li></ul>
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ToolsConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	private MessageSourceAccessor messageSource;
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
	 * the {@link MP5RemoteExecutorDialog dialog} to select the remote
	 * {@link MP5Executor executor} for MP5
	 */
	private MP5RemoteExecutorDialog mp5RemoteExecutorDialog;

	/**
	 * the {@link MP5ToolConfigPanel panel} to configure the mp5 tool
	 */
	private MP5ToolConfigPanel mp5Panel;
	/**
	 * the {@link ArtifactToolConfigPanel panel} to configure artifact
	 * detection tool
	 */
	private ArtifactToolConfigPanel artifactPanel;
	/**
	 * the {@link StagerToolConfigPanel panel} to configure automatic
	 * sleep stager tool
	 */
	private StagerToolConfigPanel stagerPanel;

	/**
	 * Constructor. Sets the parameters and initializes this panel.
	 * @param messageSource the source of messages (labels)
	 * @param fileChooser the {@link ViewerFileChooser chooser} of files
	 * @param mp5ExecutorManager the {@link MP5ExecutorManager manager} of MP5
	 * executors
	 */
	public ToolsConfigPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser, MP5ExecutorManager mp5ExecutorManager) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		this.mp5ExecutorManager = mp5ExecutorManager;
		initialize();
	}

	/**
	 * Initializes this panel with border layout and 3 sub-panels (from top to
	 * bottom:
	 * <ul>
	 * <li>the {@link MP5ToolConfigPanel panel} to configure the mp5 tool,</li>
	 * <li>the {@link ArtifactToolConfigPanel panel} to configure artifact
	 * detection tool,</li>
	 * <li>the {@link StagerToolConfigPanel panel} to configure automatic
	 * sleep stager tool.</li></ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(3,3,3,3));

		add(getMp5Panel(), BorderLayout.NORTH);
		add(getArtifactPanel(), BorderLayout.CENTER);
		add(getStagerPanel(), BorderLayout.SOUTH);

	}

	/**
	 * Returns the {@link MP5ToolConfigPanel panel} to configure the mp5 tool.
	 * If the panel doesn't exist it is created and
	 * the {@link MP5ExecutorManager executor manager} and dialogs for
	 * {@link MP5LocalExecutorDialog local} and {@link MP5RemoteExecutorDialog
	 * remote} executors are set.
	 * @return the panel to configure the mp5 tool
	 */
	public MP5ToolConfigPanel getMp5Panel() {
		if (mp5Panel == null) {
			mp5Panel = new MP5ToolConfigPanel(messageSource, mp5ExecutorManager);
			mp5Panel.setLocalExecutorDialog(mp5LocalExecutorDialog);
			mp5Panel.setRemoteExecutorDialog(mp5RemoteExecutorDialog);
		}
		return mp5Panel;
	}

	/**
	 * Returns the {@link ArtifactToolConfigPanel panel} to configure artifact
	 * detection tool.
	 * If the panel doesn't exist it is created and
	 * the {@link ViewerFileChooser file chooser} is set to it.
	 * @return the panel to configure artifact detection tool
	 */
	public ArtifactToolConfigPanel getArtifactPanel() {
		if (artifactPanel == null) {
			artifactPanel = new ArtifactToolConfigPanel(messageSource,fileChooser);
		}
		return artifactPanel;
	}

	/**
	 * Returns the {@link StagerToolConfigPanel panel} to configure automatic
	 * sleep stager tool.
	 * If the panel doesn't exist it is created and
	 * the {@link ViewerFileChooser file chooser} is set to it.
	 * @return the panel to configure automatic sleep stager tool
	 */
	public StagerToolConfigPanel getStagerPanel() {
		if (stagerPanel == null) {
			stagerPanel = new StagerToolConfigPanel(messageSource,fileChooser);
		}
		return stagerPanel;
	}

	/**
	 * Fills the dependent panels ({@link
	 * ArtifactToolConfigPanel#fillPanelFromModel(org.signalml.app.method.artifact.ArtifactConfiguration)
	 * artifact} and {@link StagerToolConfigPanel#fillPanelFromModel(
	 * org.signalml.app.method.stager.StagerConfiguration) stager}) using the
	 * given {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {

		getArtifactPanel().fillPanelFromModel(applicationConfig.getArtifactConfig());
		getStagerPanel().fillPanelFromModel(applicationConfig.getStagerConfig());

	}

	/**
	 * Stores the data from dependent panels
	 * ({@link ArtifactToolConfigPanel#fillModelFromPanel(org.signalml.app.method.artifact.ArtifactConfiguration)
	 * artifact} and {@link StagerToolConfigPanel#fillModelFromPanel(
	 * org.signalml.app.method.stager.StagerConfiguration) stager}) in the
	 * given {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) {

		getArtifactPanel().fillModelFromPanel(applicationConfig.getArtifactConfig());
		getStagerPanel().fillModelFromPanel(applicationConfig.getStagerConfig());

	}

	/**
	 * Validates this panel.
	 * This panel is valid {@link ArtifactToolConfigPanel#validatePanel(Errors)
	 * artifact} and {@link StagerToolConfigPanel#validatePanel(Errors) stager}
	 * panels are valid.
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Errors errors) {

		errors.pushNestedPath("artifactConfig");
		getArtifactPanel().validatePanel(errors);
		errors.popNestedPath();

		errors.pushNestedPath("stagerConfig");
		getStagerPanel().validatePanel(errors);
		errors.popNestedPath();

	}

	/**
	 * Returns the {@link MP5LocalExecutorDialog dialog} to select the local
	 * {@link MP5Executor executor} for MP5.
	 * @return the dialog to select the local executor for MP5
	 */
	public MP5LocalExecutorDialog getMp5LocalExecutorDialog() {
		return mp5LocalExecutorDialog;
	}

	/**
	 * Sets the {@link MP5LocalExecutorDialog dialog} to select the local
	 * {@link MP5Executor executor} for MP5.
	 * @param mp5LocalExecutorDialog the dialog to select the local executor
	 * for MP5
	 */
	public void setMp5LocalExecutorDialog(MP5LocalExecutorDialog mp5LocalExecutorDialog) {
		this.mp5LocalExecutorDialog = mp5LocalExecutorDialog;
		getMp5Panel().setLocalExecutorDialog(mp5LocalExecutorDialog);
	}

	/**
	 * Returns the {@link MP5RemoteExecutorDialog dialog} to select the remote
	 * {@link MP5Executor executor} for MP5.
	 * @return the dialog to select the remote executor for MP5
	 */
	public MP5RemoteExecutorDialog getMp5RemoteExecutorDialog() {
		return mp5RemoteExecutorDialog;
	}

	/**
	 * Sets the {@link MP5RemoteExecutorDialog dialog} to select the remote
	 * {@link MP5Executor executor} for MP5
	 * @param mp5RemoteExecutorDialog the dialog to select the remote executor
	 * for MP5
	 */
	public void setMp5RemoteExecutorDialog(MP5RemoteExecutorDialog mp5RemoteExecutorDialog) {
		this.mp5RemoteExecutorDialog = mp5RemoteExecutorDialog;
		getMp5Panel().setRemoteExecutorDialog(mp5RemoteExecutorDialog);
	}



}
