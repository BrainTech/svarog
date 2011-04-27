/* StagerToolConfigPanel.java created 2008-02-08
 *
 */
package org.signalml.app.method.stager;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.signalml.app.view.ViewerFileChooser;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerToolConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerToolConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private ViewerFileChooser fileChooser;

	private StagerToolWorkingDirectoryConfigPanel workingDirectoryPanel;

	public StagerToolConfigPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getWorkingDirectoryPanel(), BorderLayout.CENTER);

	}

	public StagerToolWorkingDirectoryConfigPanel getWorkingDirectoryPanel() {
		if (workingDirectoryPanel == null) {
			workingDirectoryPanel = new StagerToolWorkingDirectoryConfigPanel(messageSource,fileChooser);
		}
		return workingDirectoryPanel;
	}


	public void fillPanelFromModel(StagerConfiguration applicationConfig) {

		getWorkingDirectoryPanel().fillPanelFromModel(applicationConfig);

	}

	public void fillModelFromPanel(StagerConfiguration applicationConfig) {


		getWorkingDirectoryPanel().fillModelFromPanel(applicationConfig);

	}

	public void validatePanel(Errors errors) {

		getWorkingDirectoryPanel().validatePanel(errors);

	}

}
