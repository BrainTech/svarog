/* MP5BasicConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.element.TextPanePanel;
import org.signalml.method.mp5.MP5Parameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5BasicConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5BasicConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private AbstractDialog owner;

	private MP5AlgorithmConfigPanel algorithmConfigPanel;
	private MP5StoppingCriteriaConfigPanel stoppingCriteriaConfigPanel;
	private MP5DictionaryDensityConfigPanel dictionaryDensityConfigPanel;

	private TextPanePanel bookCommentPanel;

	public MP5BasicConfigPanel(MessageSourceAccessor messageSource, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		JPanel topPanel = new JPanel(new BorderLayout());

		topPanel.add(getAlgorithmConfigPanel(), BorderLayout.NORTH);
		topPanel.add(getStoppingCriteriaConfigPanel(), BorderLayout.CENTER);
		topPanel.add(getDictionaryDensityConfigPanel(), BorderLayout.SOUTH);

		JPanel bottomPanel = new JPanel(new BorderLayout(3,3));
		bottomPanel.setBorder(new TitledBorder(messageSource.getMessage("mp5Method.dialog.bookComment")));

		CompactButton bookCommentHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, MP5MethodDialog.HELP_BOOK_COMMENT);

		JPanel bookHelpPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		bookHelpPanel.add(bookCommentHelpButton);

		bottomPanel.add(getBookCommentPanel(), BorderLayout.CENTER);
		bottomPanel.add(bookHelpPanel, BorderLayout.SOUTH);

		add(topPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.CENTER);

	}

	public MP5AlgorithmConfigPanel getAlgorithmConfigPanel() {
		if (algorithmConfigPanel == null) {
			algorithmConfigPanel = new MP5AlgorithmConfigPanel(messageSource,owner);
		}
		return algorithmConfigPanel;
	}

	public TextPanePanel getBookCommentPanel() {
		if (bookCommentPanel == null) {
			bookCommentPanel = new TextPanePanel(null);
			bookCommentPanel.setPreferredSize(new Dimension(200,80));
		}
		return bookCommentPanel;
	}

	public MP5StoppingCriteriaConfigPanel getStoppingCriteriaConfigPanel() {
		if (stoppingCriteriaConfigPanel == null) {
			stoppingCriteriaConfigPanel = new MP5StoppingCriteriaConfigPanel(messageSource,owner);
		}
		return stoppingCriteriaConfigPanel;
	}

	public MP5DictionaryDensityConfigPanel getDictionaryDensityConfigPanel() {
		if (dictionaryDensityConfigPanel == null) {
			dictionaryDensityConfigPanel = new MP5DictionaryDensityConfigPanel(messageSource,owner);
		}
		return dictionaryDensityConfigPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getAlgorithmConfigPanel().fillPanelFromParameters(parameters);
		getStoppingCriteriaConfigPanel().fillPanelFromParameters(parameters);
		getDictionaryDensityConfigPanel().fillPanelFromParameters(parameters);

		getBookCommentPanel().getTextPane().setText(parameters.getBookComment());

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		getAlgorithmConfigPanel().fillParametersFromPanel(parameters);
		getStoppingCriteriaConfigPanel().fillParametersFromPanel(parameters);
		getDictionaryDensityConfigPanel().fillParametersFromPanel(parameters);

		parameters.setBookComment(getBookCommentPanel().getTextPane().getText());

	}

	public void validatePanel(Errors errors) {

		getAlgorithmConfigPanel().validatePanel(errors);
		getStoppingCriteriaConfigPanel().validatePanel(errors);
		getDictionaryDensityConfigPanel().validatePanel(errors);

		// comment panel is ok

	}

}
