/* NewStagerToolConfigPanel.java created 2008-02-08
 * 
 */
package org.signalml.plugin.newstager.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.newstager.data.NewStagerConfiguration;

/**
 * NewStagerToolConfigPanel
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerToolConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private FileChooser fileChooser;

	private NewStagerToolWorkingDirectoryConfigPanel workingDirectoryPanel;

	public NewStagerToolConfigPanel(FileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getWorkingDirectoryPanel(), BorderLayout.CENTER);

	}

	public NewStagerToolWorkingDirectoryConfigPanel getWorkingDirectoryPanel() {
		if (workingDirectoryPanel == null) {
			workingDirectoryPanel = new NewStagerToolWorkingDirectoryConfigPanel(
					fileChooser);
		}
		return workingDirectoryPanel;
	}

	public void fillPanelFromModel(NewStagerConfiguration applicationConfig) {
		getWorkingDirectoryPanel().fillPanelFromModel(applicationConfig);
	}

	public void fillModelFromPanel(NewStagerConfiguration applicationConfig) {
		getWorkingDirectoryPanel().fillModelFromPanel(applicationConfig);
	}

	public void validatePanel(ValidationErrors errors) {
		getWorkingDirectoryPanel().validatePanel(errors);
	}

}
