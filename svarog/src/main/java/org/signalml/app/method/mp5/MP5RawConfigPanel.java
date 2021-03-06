/* MP5RawConfigPanel.java created 2008-01-31
 *
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.AnyChangeDocumentAdapter;
import org.signalml.app.view.common.components.panels.TextPanePanel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

/** MP5RawConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5RawConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MP5ExecutorManager executorManager;
	private AbstractDialog owner;

	private TextPanePanel rawConfigTextPane;
	private MP5ExecutorPanel executorPanel;

	private boolean configChanged;

	public MP5RawConfigPanel(MP5ExecutorManager executorManager, AbstractDialog owner) {
		super();
		this.executorManager = executorManager;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		JPanel rawConfigPanel = new JPanel(new BorderLayout(3,3));
		rawConfigPanel.setBorder(new TitledBorder(_("Raw config (signal input/output/range directives will be added/replaced)")));

//		CompactButton rawConfigHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_RAW_CONFIG);

//		JPanel rawConfigHelpPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//		rawConfigHelpPanel.add(rawConfigHelpButton);

		rawConfigPanel.add(getRawConfigTextPane(), BorderLayout.CENTER);
//		rawConfigPanel.add(rawConfigHelpPanel, BorderLayout.SOUTH);

		add(rawConfigPanel, BorderLayout.CENTER);
		add(getExecutorPanel(), BorderLayout.SOUTH);

	}

	public TextPanePanel getRawConfigTextPane() {
		if (rawConfigTextPane == null) {
			rawConfigTextPane = new TextPanePanel(null);
			rawConfigTextPane.setPreferredSize(new Dimension(200,150));

			rawConfigTextPane.getTextPane().getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {

				@Override
				public void anyUpdate(DocumentEvent e) {
					configChanged = true;
				}

			});
		}
		return rawConfigTextPane;
	}

	public MP5ExecutorPanel getExecutorPanel() {
		if (executorPanel == null) {
			executorPanel = new MP5ExecutorPanel(executorManager);
		}
		return executorPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getRawConfigTextPane().getTextPane().setText(parameters.getRawConfigText());

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setRawConfigText(getRawConfigTextPane().getTextPane().getText().trim());

	}

	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

	public boolean isConfigChanged() {
		return configChanged;
	}

	public void setConfigChanged(boolean configChanged) {
		this.configChanged = configChanged;
	}

}
