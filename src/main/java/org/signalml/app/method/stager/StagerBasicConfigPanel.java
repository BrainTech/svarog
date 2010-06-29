/* StagerBasicConfigPanel.java created 2008-02-14
 *
 */
package org.signalml.app.method.stager;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.method.stager.StagerParameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerBasicConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerBasicConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private AbstractDialog owner;
	private ViewerFileChooser fileChooser;

	private StagerBookPanel bookPanel;
	private StagerBasicParametersPanel parametersPanel;
	private StagerEnableAdvancedConfigPanel enableAdvancedConfigPanel;

	public StagerBasicConfigPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getBookPanel(), BorderLayout.NORTH);

		StagerBasicParametersPanel paramPanel = getParametersPanel();

		JPanel centerEnclosurePanel = new JPanel(new BorderLayout());
		centerEnclosurePanel.setBorder(paramPanel.getBorder());
		paramPanel.setBorder(null);

		centerEnclosurePanel.add(paramPanel, BorderLayout.NORTH);
		centerEnclosurePanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		add(centerEnclosurePanel, BorderLayout.CENTER);
		add(getEnableAdvancedConfigPanel(), BorderLayout.SOUTH);

	}

	public StagerBookPanel getBookPanel() {
		if (bookPanel == null) {
			bookPanel = new StagerBookPanel(messageSource,fileChooser);
		}
		return bookPanel;
	}

	public StagerBasicParametersPanel getParametersPanel() {
		if (parametersPanel == null) {
			parametersPanel = new StagerBasicParametersPanel(messageSource, owner);
		}
		return parametersPanel;
	}

	public StagerEnableAdvancedConfigPanel getEnableAdvancedConfigPanel() {
		if (enableAdvancedConfigPanel == null) {
			enableAdvancedConfigPanel = new StagerEnableAdvancedConfigPanel(messageSource, getParametersPanel().getAmplitudePanelsEnable());
		}
		return enableAdvancedConfigPanel;
	}

	public void fillPanelFromParameters(StagerParameters parameters) {

		getBookPanel().fillPanelFromModel(parameters);
		getParametersPanel().fillPanelFromParameters(parameters);
		getEnableAdvancedConfigPanel().fillPanelFromParameters(parameters);

	}

	public void fillParametersFromPanel(StagerParameters parameters) {

		getBookPanel().fillModelFromPanel(parameters);
		getParametersPanel().fillParametersFromPanel(parameters);
		getEnableAdvancedConfigPanel().fillParametersFromPanel(parameters);

	}

	public void validatePanel(Errors errors) {

		getBookPanel().validatePanel(errors);
		getParametersPanel().validatePanel(errors);
		getEnableAdvancedConfigPanel().validatePanel(errors);

	}

}
