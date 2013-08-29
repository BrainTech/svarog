/* MP5ExpertConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.common.components.panels.TextPanePanel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

/** MP5ExpertConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExpertConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MP5ExecutorManager executorManager;
	private AbstractDialog owner;

	private TextPanePanel additionalConfigTextPane;
	private TextPanePanel bookCommentPanel;
	private MP5ExecutorPanel executorPanel;

	public MP5ExpertConfigPanel(MP5ExecutorManager executorManager, AbstractDialog owner) {
		super();
		this.owner = owner;
		this.executorManager = executorManager;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		JPanel additionalConfigPanel = new JPanel(new BorderLayout(3,3));
		additionalConfigPanel.setBorder(new TitledBorder(_("Additional config (will be appended to config file as is)")));

//		CompactButton additionalConfigHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_ADDITIONAL_CONFIG);

//		JPanel additionalConfigHelpPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//		additionalConfigHelpPanel.add(additionalConfigHelpButton);

		additionalConfigPanel.add(getAdditionalConfigTextPane(), BorderLayout.CENTER);
//		additionalConfigPanel.add(additionalConfigHelpPanel, BorderLayout.SOUTH);

		JPanel bottomPanel = new JPanel(new BorderLayout(3,3));
		bottomPanel.setBorder(new TitledBorder(_("Book comment")));

//		CompactButton bookCommentHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_BOOK_COMMENT);

//		JPanel bookHelpPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//		bookHelpPanel.add(bookCommentHelpButton);

		bottomPanel.add(getBookCommentPanel(), BorderLayout.CENTER);
//		bottomPanel.add(bookHelpPanel, BorderLayout.SOUTH);

		add(additionalConfigPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.CENTER);
		add(getExecutorPanel(), BorderLayout.SOUTH);

	}

	public TextPanePanel getAdditionalConfigTextPane() {
		if (additionalConfigTextPane == null) {
			additionalConfigTextPane = new TextPanePanel(null);
			additionalConfigTextPane.setPreferredSize(new Dimension(200,150));
		}
		return additionalConfigTextPane;
	}

	public TextPanePanel getBookCommentPanel() {
		if (bookCommentPanel == null) {
			bookCommentPanel = new TextPanePanel(null);
			bookCommentPanel.setPreferredSize(new Dimension(200,80));
		}
		return bookCommentPanel;
	}

	public MP5ExecutorPanel getExecutorPanel() {
		if (executorPanel == null) {
			executorPanel = new MP5ExecutorPanel(executorManager);
		}
		return executorPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getAdditionalConfigTextPane().getTextPane().setText(parameters.getCustomConfigText());
		getBookCommentPanel().getTextPane().setText(parameters.getBookComment());

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setCustomConfigText(getAdditionalConfigTextPane().getTextPane().getText().trim());
		parameters.setBookComment(getBookCommentPanel().getTextPane().getText());

	}

	public void validatePanel(ValidationErrors errors) {

		// additional config panel is ok

	}

}
