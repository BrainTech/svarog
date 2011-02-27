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

/**
 * Panel which allows to select the {@link SignalSourceLevel level} of
 * processing of the signal that should be used.
 * Contains 3 buttons:
 * <ul>
 * <li>the radio button which tells that the {@link SignalSourceLevel#ASSEMBLED
 * ASSEMBLED} signal should be used,</li>
 * <li>the radio button which tells that the {@link SignalSourceLevel#FILTERED
 * FILTERED} signal should be used.</li>
 * <li>the radio button which tells that the {@link SignalSourceLevel#RAW RAW}
 * signal should be used (currently this button is not used).</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSourceLevelPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalSourceLevelPanel.class);

	/**
	 * the source of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * the radio button which tells that the {@link SignalSourceLevel#RAW RAW}
	 * signal should be used;
	 * for a time being the button is not used 
	 */
	private JRadioButton rawRadioButton;
	/**
	 * the radio button which tells that the {@link SignalSourceLevel#ASSEMBLED
	 * ASSEMBLED} signal should be used
	 */
	private JRadioButton assembledRadioButton;
	/**
	 * the radio button which tells that the {@link SignalSourceLevel#FILTERED
	 * FILTERED} signal should be used
	 */
	private JRadioButton filteredRadioButton;

	/**
	 * the group of radio buttons which allow to select the {@link
	 * SignalSourceLevel level} of the signal
	 * ({@link #rawRadioButton RAW}, {@link #assembledRadioButton ASSEMBLED}
	 * and {@link #filteredRadioButton FILTERED})
	 */
	private ButtonGroup buttonGroup;

	public SignalSourceLevelPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;

		initialize();
	}

	/**
	 * Initilizes this panel with FlowLayout and two buttons:
	 * <ul>
	 * <li>the radio button which tells that the {@link SignalSourceLevel#ASSEMBLED
	 * ASSEMBLED} signal should be used,</li>
	 * <li>the radio button which tells that the {@link SignalSourceLevel#FILTERED
	 * FILTERED} signal should be used.</li></ul>
	 */
	private void initialize() {

		buttonGroup = new ButtonGroup();

		setLayout(new FlowLayout(FlowLayout.CENTER));

		setBorder(new TitledBorder(messageSource.getMessage("signalSpace.signalLevelType.title")));

		//removed - m 0006280
		//add( getRawRadioButton() );
		add(getAssembledRadioButton());
		add(getFilteredRadioButton());

	}

	/**
	 * Returns the radio button which tells that the
	 * {@link SignalSourceLevel#RAW RAW} signal should be used.
	 * For a time being the button is not used. 
	 * If the button doesn't exist it is created and added to the group of
	 * buttons.
	 * @return the radio button which tells that the RAW signal should be used
	 */
	public JRadioButton getRawRadioButton() {
		if (rawRadioButton == null) {
			rawRadioButton = new JRadioButton(messageSource.getMessage("signalSpace.signalLevelType.rawSignal"));

			buttonGroup.add(rawRadioButton);
		}
		return rawRadioButton;
	}

	/**
	 * Returns the radio button which tells that the
	 * {@link SignalSourceLevel#ASSEMBLED ASSEMBLED} signal should be used.
	 * If the button doesn't exist it is created and added to the group of
	 * buttons.
	 * @return the radio button which tells that the ASSEMBLED signal should
	 * be used
	 */
	public JRadioButton getAssembledRadioButton() {
		if (assembledRadioButton == null) {
			assembledRadioButton = new JRadioButton(messageSource.getMessage("signalSpace.signalLevelType.assembledSignal"));

			buttonGroup.add(assembledRadioButton);
		}
		return assembledRadioButton;
	}

	/**
	 * Returns the radio button which tells that the
	 * {@link SignalSourceLevel#FILTERED FILTERED} signal should be used.
	 * If the button doesn't exist it is created and added to the group of
	 * buttons.
	 * @return the radio button which tells that the FILTERED signal should be
	 * used
	 */
	public JRadioButton getFilteredRadioButton() {
		if (filteredRadioButton == null) {
			filteredRadioButton = new JRadioButton(messageSource.getMessage("signalSpace.signalLevelType.filteredSignal"));

			buttonGroup.add(filteredRadioButton);
		}
		return filteredRadioButton;
	}

	/**
	 * Depending on the {@link SignalSourceLevel level}
	 * {@link SignalSpace#getSignalSourceLevel() obtained} from the
	 * {@link SignalSpace model}
	 * sets the appropriate button to be active.
	 * @param space the signal space
	 */
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

	/**
	 * {@link SignalSpace#setSignalSourceLevel(SignalSourceLevel) sets} the
	 * {@link SignalSourceLevel level} in the {@link SignalSpace model}
	 * depending on the selected button.
	 * @param space the signal space
	 */
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

	/**
	 * Validates this panel.
	 * This panel is always valid.
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Errors errors) {
		// nothing to do
	}

}
