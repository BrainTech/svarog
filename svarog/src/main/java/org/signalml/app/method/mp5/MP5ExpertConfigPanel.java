/* MP5ExpertConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
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
//	private TextPanePanel bookCommentPanel;
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
		additionalConfigPanel.add(getAdditionalConfigTextPane(), BorderLayout.CENTER);

//		JPanel bottomPanel = new JPanel(new BorderLayout(3,3));
//		bottomPanel.setBorder(new TitledBorder(_("Book comment")));
//		bottomPanel.add(getBookCommentPanel(), BorderLayout.CENTER);

		add(additionalConfigPanel, BorderLayout.CENTER);
//		add(bottomPanel, BorderLayout.CENTER);
		add(getExecutorPanel(), BorderLayout.SOUTH);

	}

	public TextPanePanel getAdditionalConfigTextPane() {
		if (additionalConfigTextPane == null) {
			additionalConfigTextPane = new TextPanePanel(null);
			additionalConfigTextPane.setPreferredSize(new Dimension(200,150));
		}
		return additionalConfigTextPane;
	}

//	public TextPanePanel getBookCommentPanel() {
//		if (bookCommentPanel == null) {
//			bookCommentPanel = new TextPanePanel(null);
//			bookCommentPanel.setPreferredSize(new Dimension(200,80));
//		}
//		return bookCommentPanel;
//	}

	public MP5ExecutorPanel getExecutorPanel() {
		if (executorPanel == null) {
			executorPanel = new MP5ExecutorPanel(executorManager);
		}
		return executorPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getAdditionalConfigTextPane().getTextPane().setText(parameters.getCustomConfigText());
//		getBookCommentPanel().getTextPane().setText(parameters.getBookComment());

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setCustomConfigText(getAdditionalConfigTextPane().getTextPane().getText().trim());
//		parameters.setBookComment(getBookCommentPanel().getTextPane().getText());

	}

	public void validatePanel(ValidationErrors errors) {

		// additional config panel is ok

	}

}
