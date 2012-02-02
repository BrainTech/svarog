/* ToolsConfigPanel.java created 2007-12-14
 *
 */
package org.signalml.app.view.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.method.mp5.MP5ToolConfigPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.mp5.MP5Executor;

import org.springframework.validation.Errors;

/**
 * The panel which allows to configure 1 tool for Svarog:
 * <ul>
 * <li>the {@link MP5ToolConfigPanel mp5 tool},</li>
 * </ul>
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ToolsConfigPanel extends JPanel {

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
	 * the {@link MP5ToolConfigPanel panel} to configure the mp5 tool
	 */
	private MP5ToolConfigPanel mp5Panel;

	/**
	 * Constructor. Sets the parameters and initializes this panel.
	 * @param fileChooser the {@link ViewerFileChooser chooser} of files
	 * @param mp5ExecutorManager the {@link MP5ExecutorManager manager} of MP5
	 * executors
	 */
	public ToolsConfigPanel(ViewerFileChooser fileChooser, MP5ExecutorManager mp5ExecutorManager) {
		super();
		this.fileChooser = fileChooser;
		this.mp5ExecutorManager = mp5ExecutorManager;
		initialize();
	}

	/**
	 * Initializes this panel with border layout and 1 sub-panel (from top to
	 * bottom:
	 * <ul>
	 * <li>the {@link MP5ToolConfigPanel panel} to configure the mp5 tool</li>
	 * </ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(3,3,3,3));

		add(getMp5Panel(), BorderLayout.NORTH);
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
			mp5Panel = new MP5ToolConfigPanel(mp5ExecutorManager);
			mp5Panel.setLocalExecutorDialog(mp5LocalExecutorDialog);
		}
		return mp5Panel;
	}


	@Deprecated
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) { }

	@Deprecated
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) { }

	@Deprecated
	public void validatePanel(ValidationErrors errors) { }

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

}
