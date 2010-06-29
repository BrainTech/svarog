/* SignalSourceLevelPanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.element;

import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.domain.signal.space.SignalSpace;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** SignalSourceLevelPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSourceLevelPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalSourceLevelPanel.class);

	private MessageSourceAccessor messageSource;

	private JRadioButton rawRadioButton;
	private JRadioButton assembledRadioButton;
	private JRadioButton filteredRadioButton;

	private ButtonGroup buttonGroup;

	public SignalSourceLevelPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;

		initialize();
	}

	private void initialize() {

		buttonGroup = new ButtonGroup();

		setLayout(new FlowLayout(FlowLayout.CENTER));

		setBorder(new TitledBorder(messageSource.getMessage("signalSpace.signalLevelType.title")));

		//removed - m 0006280
		//add( getRawRadioButton() );
		add(getAssembledRadioButton());
		add(getFilteredRadioButton());

	}

	public JRadioButton getRawRadioButton() {
		if (rawRadioButton == null) {
			rawRadioButton = new JRadioButton(messageSource.getMessage("signalSpace.signalLevelType.rawSignal"));

			buttonGroup.add(rawRadioButton);
		}
		return rawRadioButton;
	}

	public JRadioButton getAssembledRadioButton() {
		if (assembledRadioButton == null) {
			assembledRadioButton = new JRadioButton(messageSource.getMessage("signalSpace.signalLevelType.assembledSignal"));

			buttonGroup.add(assembledRadioButton);
		}
		return assembledRadioButton;
	}

	public JRadioButton getFilteredRadioButton() {
		if (filteredRadioButton == null) {
			filteredRadioButton = new JRadioButton(messageSource.getMessage("signalSpace.signalLevelType.filteredSignal"));

			buttonGroup.add(filteredRadioButton);
		}
		return filteredRadioButton;
	}

	public void fillPanelFromModel(SignalSpace space) {

		SignalSourceLevel signalSourceLevel = space.getSignalSourceLevel();

		switch (signalSourceLevel) {

		case ASSEMBLED:
			getAssembledRadioButton().setSelected(true);
			break;

		case FILTERED:
			getFilteredRadioButton().setSelected(true);
			break;

		case RAW:
		default:
			getRawRadioButton().setSelected(true);
			break;

		}

	}

	public void fillModelFromPanel(SignalSpace space) {

		if (getFilteredRadioButton().isSelected()) {
			space.setSignalSourceLevel(SignalSourceLevel.FILTERED);
		}
		else if (getAssembledRadioButton().isSelected()) {
			space.setSignalSourceLevel(SignalSourceLevel.ASSEMBLED);
		} else {
			space.setSignalSourceLevel(SignalSourceLevel.RAW);
		}

	}

	public void validatePanel(Errors errors) {
		// nothing to do
	}

}
