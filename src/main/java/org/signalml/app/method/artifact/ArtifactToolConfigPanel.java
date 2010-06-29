/* ArtifactToolConfigPanel.java created 2008-02-08
 *
 */
package org.signalml.app.method.artifact;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.signalml.app.view.ViewerFileChooser;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ArtifactToolConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactToolConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private ViewerFileChooser fileChooser;

	private ArtifactToolWorkingDirectoryConfigPanel workingDirectoryPanel;

	public ArtifactToolConfigPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getWorkingDirectoryPanel(), BorderLayout.CENTER);

	}

	public ArtifactToolWorkingDirectoryConfigPanel getWorkingDirectoryPanel() {
		if (workingDirectoryPanel == null) {
			workingDirectoryPanel = new ArtifactToolWorkingDirectoryConfigPanel(messageSource,fileChooser);
		}
		return workingDirectoryPanel;
	}


	public void fillPanelFromModel(ArtifactConfiguration applicationConfig) {

		getWorkingDirectoryPanel().fillPanelFromModel(applicationConfig);

	}

	public void fillModelFromPanel(ArtifactConfiguration applicationConfig) {


		getWorkingDirectoryPanel().fillModelFromPanel(applicationConfig);

	}

	public void validatePanel(Errors errors) {

		getWorkingDirectoryPanel().validatePanel(errors);

	}

}
