/* MP5DictionaryConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

/** MP5DictionaryConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5DictionaryConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractDialog owner;

	private MP5DictionaryDensityConfigPanel dictionaryDensityConfigPanel;
	private MP5AdvancedDecompositionConfigPanel advancedDecompositionConfigPanel;
	private MP5AtomsInDictionaryPanel outputConfigPanel;

	public MP5DictionaryConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		add(getDictionaryDensityConfigPanel(), BorderLayout.NORTH);
		add(getAdvancedDecompositionConfigPanel(), BorderLayout.CENTER);
		add(getOutputConfigPanel(), BorderLayout.SOUTH);

	}

	public MP5DictionaryDensityConfigPanel getDictionaryDensityConfigPanel() {
		if (dictionaryDensityConfigPanel == null) {
			dictionaryDensityConfigPanel = new MP5DictionaryDensityConfigPanel(owner);
		}
		return dictionaryDensityConfigPanel;
	}

	public MP5AdvancedDecompositionConfigPanel getAdvancedDecompositionConfigPanel() {
		if (advancedDecompositionConfigPanel == null) {
			advancedDecompositionConfigPanel = new MP5AdvancedDecompositionConfigPanel(owner);
		}
		return advancedDecompositionConfigPanel;
	}

	public MP5AtomsInDictionaryPanel getOutputConfigPanel() {
		if (outputConfigPanel == null) {
			outputConfigPanel = new MP5AtomsInDictionaryPanel();
		}
		return outputConfigPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getDictionaryDensityConfigPanel().fillPanelFromParameters(parameters);
		getAdvancedDecompositionConfigPanel().fillPanelFromParameters(parameters);
		getOutputConfigPanel().fillPanelFromParameters(parameters);

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		getDictionaryDensityConfigPanel().fillParametersFromPanel(parameters);
		getAdvancedDecompositionConfigPanel().fillParametersFromPanel(parameters);
		getOutputConfigPanel().fillParametersFromPanel(parameters);

	}

	public void validatePanel(ValidationErrors errors) {

		getDictionaryDensityConfigPanel().validatePanel(errors);
		getAdvancedDecompositionConfigPanel().validatePanel(errors);
		getOutputConfigPanel().validatePanel(errors);

		// comment panel is ok

	}

}
