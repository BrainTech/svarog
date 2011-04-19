/* ArtifactToolConfigPanel.java created 2008-02-08
 *
 */
package org.signalml.plugin.newartifact.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.signalml.app.view.ViewerFileChooser;
import org.signalml.plugin.newartifact.data.NewArtifactConfiguration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ArtifactToolConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactToolConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private ViewerFileChooser fileChooser;

	private NewArtifactToolWorkingDirectoryConfigPanel workingDirectoryPanel;

	public NewArtifactToolConfigPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getWorkingDirectoryPanel(), BorderLayout.CENTER);

	}

	public NewArtifactToolWorkingDirectoryConfigPanel getWorkingDirectoryPanel() {
		if (workingDirectoryPanel == null) {
			workingDirectoryPanel = new NewArtifactToolWorkingDirectoryConfigPanel(messageSource,fileChooser);
		}
		return workingDirectoryPanel;
	}


	public void fillPanelFromModel(NewArtifactConfiguration applicationConfig) {

		getWorkingDirectoryPanel().fillPanelFromModel(applicationConfig);

	}

	public void fillModelFromPanel(NewArtifactConfiguration applicationConfig) {


		getWorkingDirectoryPanel().fillModelFromPanel(applicationConfig);

	}

	public void validatePanel(Errors errors) {

		getWorkingDirectoryPanel().validatePanel(errors);

	}

}