/* StagerBasicConfigPanel.java created 2008-02-14
 * 
 */
package org.signalml.plugin.newstager.ui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.plugin.export.view.AbstractPluginDialog;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.springframework.validation.Errors;

/**
 * StagerBasicConfigPanel
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerBasicConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AbstractDialog owner;
	private FileChooser fileChooser;

	private NewStagerBookPanel bookPanel;
	private NewStagerBasicParametersPanel parametersPanel;
	private NewStagerEnableAdvancedConfigPanel enableAdvancedConfigPanel;

	public NewStagerBasicConfigPanel(FileChooser fileChooser,
			AbstractDialog owner) {
		super();
		this.fileChooser = fileChooser;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getBookPanel(), BorderLayout.NORTH);

		NewStagerBasicParametersPanel paramPanel = getParametersPanel();

		JPanel centerEnclosurePanel = new JPanel(new BorderLayout());
		centerEnclosurePanel.setBorder(paramPanel.getBorder());
		paramPanel.setBorder(null);

		centerEnclosurePanel.add(paramPanel, BorderLayout.NORTH);
		centerEnclosurePanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		add(centerEnclosurePanel, BorderLayout.CENTER);
		add(getEnableAdvancedConfigPanel(), BorderLayout.SOUTH);

	}

	public NewStagerBookPanel getBookPanel() {
		if (bookPanel == null) {
			bookPanel = new NewStagerBookPanel(fileChooser);
		}
		return bookPanel;
	}

	public NewStagerBasicParametersPanel getParametersPanel() {
		if (parametersPanel == null) {
			parametersPanel = new NewStagerBasicParametersPanel(owner);
		}
		return parametersPanel;
	}

	public NewStagerEnableAdvancedConfigPanel getEnableAdvancedConfigPanel() {
		if (enableAdvancedConfigPanel == null) {
			enableAdvancedConfigPanel = null;//TODO!//new NewStagerEnableAdvancedConfigPanel(getParametersPanel().getAmplitudePanelsEnable());
		}
		return enableAdvancedConfigPanel;
	}

	public void fillPanelFromParameters(NewStagerParameters parameters) {
		getBookPanel().fillPanelFromModel(parameters);
		getParametersPanel().fillPanelFromParameters(parameters);
		getEnableAdvancedConfigPanel().fillPanelFromParameters(parameters);
	}

	public void fillParametersFromPanel(NewStagerParameters parameters) {
		getBookPanel().fillModelFromPanel(parameters);
		getParametersPanel().fillParametersFromPanel(parameters);
		getEnableAdvancedConfigPanel().fillParametersFromPanel(parameters);

	}

	public void validatePanel(ValidationErrors errors) {
		getBookPanel().validatePanel(errors);
		getParametersPanel().validatePanel(errors);
		getEnableAdvancedConfigPanel().validatePanel(errors);
	}

}
