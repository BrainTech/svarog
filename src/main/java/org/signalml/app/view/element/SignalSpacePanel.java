/* SignalSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** SignalSpacePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalSpacePanel.class);

	private MessageSourceAccessor messageSource;

	private TimeSpacePanel timeSpacePanel;
	private SignalSourceLevelPanel signalSourceLevelPanel;
	private ChannelSpacePanel channelSpacePanel;

	public SignalSpacePanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		JPanel westPanel = new JPanel(new BorderLayout());

		westPanel.add(getTimeSpacePanel(), BorderLayout.CENTER);
		final SignalSourceLevelPanel sourceLevelPanel = getSignalSourceLevelPanel();
		westPanel.add(sourceLevelPanel, BorderLayout.SOUTH);

		add(westPanel, BorderLayout.CENTER);
		add(getChannelSpacePanel(), BorderLayout.EAST);

		ItemListener itemListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (selected) {

					if (sourceLevelPanel.getFilteredRadioButton().isSelected()) {
						getChannelSpacePanel().setCurrentLevel(SignalSourceLevel.FILTERED);
					}
					else if (sourceLevelPanel.getAssembledRadioButton().isSelected()) {
						getChannelSpacePanel().setCurrentLevel(SignalSourceLevel.ASSEMBLED);
					} else {
						getChannelSpacePanel().setCurrentLevel(SignalSourceLevel.RAW);
					}

				}

			}

		};

		sourceLevelPanel.getRawRadioButton().addItemListener(itemListener);
		sourceLevelPanel.getAssembledRadioButton().addItemListener(itemListener);
		sourceLevelPanel.getFilteredRadioButton().addItemListener(itemListener);

	}

	public TimeSpacePanel getTimeSpacePanel() {
		if (timeSpacePanel == null) {
			timeSpacePanel = new TimeSpacePanel(messageSource);
		}
		return timeSpacePanel;
	}

	public SignalSourceLevelPanel getSignalSourceLevelPanel() {
		if (signalSourceLevelPanel == null) {
			signalSourceLevelPanel = new SignalSourceLevelPanel(messageSource);
		}
		return signalSourceLevelPanel;
	}

	public ChannelSpacePanel getChannelSpacePanel() {
		if (channelSpacePanel == null) {
			channelSpacePanel = new ChannelSpacePanel(messageSource);
		}
		return channelSpacePanel;
	}

	public void fillPanelFromModel(SignalSpace space) {

		getTimeSpacePanel().fillPanelFromModel(space);
		getSignalSourceLevelPanel().fillPanelFromModel(space);
		getChannelSpacePanel().fillPanelFromModel(space);

	}

	public void fillModelFromPanel(SignalSpace space) {

		getTimeSpacePanel().fillModelFromPanel(space);
		getSignalSourceLevelPanel().fillModelFromPanel(space);
		getChannelSpacePanel().fillModelFromPanel(space);

	}

	public void setConstraints(SignalSpaceConstraints constraints) {

		getTimeSpacePanel().setConstraints(constraints);
		getChannelSpacePanel().setConstraints(constraints);

	}

	public void validatePanel(Errors errors) {

		getTimeSpacePanel().validatePanel(errors);
		getSignalSourceLevelPanel().validatePanel(errors);
		getChannelSpacePanel().validatePanel(errors);

	}

}
