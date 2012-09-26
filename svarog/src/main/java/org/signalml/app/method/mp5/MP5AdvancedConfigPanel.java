/* MP5AdvancedConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JPanel;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

import org.springframework.validation.Errors;

/** MP5AdvancedConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5AdvancedConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MP5ExecutorManager executorManager;
	private AbstractDialog owner;

	private MP5AdvancedDecompositionConfigPanel advancedDecompositionConfigPanel;
	private MP5ExecutorPanel executorPanel;
	private MP5AtomsInDictionaryPanel outputConfigPanel;

	public MP5AdvancedConfigPanel(MP5ExecutorManager executorManager, AbstractDialog owner) {
		super();
		this.executorManager = executorManager;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		JPanel topPanel = new JPanel(new BorderLayout());

		topPanel.add(getAdvancedDecompositionConfigPanel(), BorderLayout.NORTH);
		topPanel.add(getExecutorPanel(), BorderLayout.CENTER);
		topPanel.add(getOutputConfigPanel(), BorderLayout.SOUTH);

		add(topPanel, BorderLayout.NORTH);
		add(Box.createVerticalGlue(), BorderLayout.CENTER);

	}

	public MP5AdvancedDecompositionConfigPanel getAdvancedDecompositionConfigPanel() {
		if (advancedDecompositionConfigPanel == null) {
			advancedDecompositionConfigPanel = new MP5AdvancedDecompositionConfigPanel(owner);
		}
		return advancedDecompositionConfigPanel;
	}

	public MP5ExecutorPanel getExecutorPanel() {
		if (executorPanel == null) {
			executorPanel = new MP5ExecutorPanel(executorManager);
		}
		return executorPanel;
	}

	public MP5AtomsInDictionaryPanel getOutputConfigPanel() {
		if (outputConfigPanel == null) {
			outputConfigPanel = new MP5AtomsInDictionaryPanel();
		}
		return outputConfigPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getAdvancedDecompositionConfigPanel().fillPanelFromParameters(parameters);
		getOutputConfigPanel().fillPanelFromParameters(parameters);

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		getAdvancedDecompositionConfigPanel().fillParametersFromPanel(parameters);
		getOutputConfigPanel().fillParametersFromPanel(parameters);

	}

	public void validatePanel(ValidationErrors errors) {

		getAdvancedDecompositionConfigPanel().validatePanel(errors);
		getOutputConfigPanel().validatePanel(errors);

	}

}
