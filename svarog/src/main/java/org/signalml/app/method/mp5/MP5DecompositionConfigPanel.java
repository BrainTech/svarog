/* MP5DecompositionConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

/** MP5DecompositionConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5DecompositionConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractDialog owner;

	private MP5AlgorithmConfigPanel algorithmConfigPanel;
	private MP5StoppingCriteriaConfigPanel stoppingCriteriaConfigPanel;

	public MP5DecompositionConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getAlgorithmConfigPanel(), BorderLayout.NORTH);
		add(getStoppingCriteriaConfigPanel(), BorderLayout.CENTER);

	}

	public MP5AlgorithmConfigPanel getAlgorithmConfigPanel() {
		if (algorithmConfigPanel == null) {
			algorithmConfigPanel = new MP5AlgorithmConfigPanel(owner);
		}
		return algorithmConfigPanel;
	}

	public MP5StoppingCriteriaConfigPanel getStoppingCriteriaConfigPanel() {
		if (stoppingCriteriaConfigPanel == null) {
			stoppingCriteriaConfigPanel = new MP5StoppingCriteriaConfigPanel(owner);
		}
		return stoppingCriteriaConfigPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getAlgorithmConfigPanel().fillPanelFromParameters(parameters);
		getStoppingCriteriaConfigPanel().fillPanelFromParameters(parameters);

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		getAlgorithmConfigPanel().fillParametersFromPanel(parameters);
		getStoppingCriteriaConfigPanel().fillParametersFromPanel(parameters);

	}

	public void validatePanel(ValidationErrors errors) {

		getAlgorithmConfigPanel().validatePanel(errors);
		getStoppingCriteriaConfigPanel().validatePanel(errors);

	}

}
