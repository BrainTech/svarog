/* SignalSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.signal.signalselection;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.signal.export.SignalSourceLevelPanel;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.springframework.validation.Errors;


/**
 * Panel that allows the user to select the part of the signal:
 * <ul>
 * <li>the time interval of the signal - see {@link TimeSpacePanel},</li>
 * <li>the level of processing of the signal - see
 * {@link SignalSourceLevelPanel},</li>
 * <li>the channels - see {@link ChannelSpacePanel}</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalSpacePanel.class);

	/**
	 * the {@link TimeSpacePanel panel} to select the time interval of the
	 * signal
	 */
	protected TimeSpacePanel timeSpacePanel;

	/**
	 * the {@link ChannelSpacePanel panel} to select the channels
	 */
	private ChannelSpacePanel channelSpacePanel;

	/**
	 * the {@link SignalSourceLevelPanel panel} to select the level of
	 * processing of the signal
	 */
	private SignalSourceLevelPanel signalSourceLevelPanel;

	/**
	 * Constructor. Initializes the panel.
	 */
	public SignalSpacePanel() {
		super();
		initialize();
	}

	/**
	 * Adds two panels:
	 * <ul>
	 * <li>"west panel" on the left, which contains (from top to bottom):
	 * <ul><li>the {@link TimeSpacePanel panel} to select the time interval of
	 * the,</li>
	 * <li>the {@link ChannelSpacePanel panel} to select the channels.</li>
	 * </ul>
	 */
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

	/**
	 * Returns the {@link TimeSpacePanel panel} to select the time interval of
	 * the signal.
	 * If the panel doesn't exist, it is created.
	 * @return the panel to select the time interval of
	 * the signal.
	 */
	public TimeSpacePanel getTimeSpacePanel() {
		if (timeSpacePanel == null) {
			timeSpacePanel = new TimeSpacePanel();
		}
		return timeSpacePanel;
	}

	/**
	 * Returns the {@link ChannelSpacePanel panel} to select the channels.
	 * If the panel doesn't exist, it is created.
	 * @return the panel to select the channels
	 */
	public ChannelSpacePanel getChannelSpacePanel() {
		if (channelSpacePanel == null) {
			channelSpacePanel = new ChannelSpacePanel();
		}
		return channelSpacePanel;
	}

	/**
        * Returns the {@link SignalSourceLevelPanel panel} to select the level of
        * processing of the signal.
        * If the panel doesn't exist, it is created.
        * @return the panel to select the level of
        * processing of the signal
        */
       public SignalSourceLevelPanel getSignalSourceLevelPanel() {
               if (signalSourceLevelPanel == null) {
                       signalSourceLevelPanel = new SignalSourceLevelPanel();
               }
               return signalSourceLevelPanel;
       }


	/**
	 * Fills fields of dependent panels
	 * ({@link TimeSpacePanel#fillPanelFromModel(SignalSpace) TimeSpacePanel},
	 * {@link SignalSourceLevelPanel#fillPanelFromModel(SignalSpace)
	 * SignalSourceLevelPanel} and
	 * {@link ChannelSpacePanel#fillPanelFromModel(SignalSpace)
	 * ChannelSpacePanel}) using the given {@link SignalSpace signal space}.
	 * @param space the signal space
	 */
	public void fillPanelFromModel(SignalSpace space) {

		getTimeSpacePanel().fillPanelFromModel(space);
		getChannelSpacePanel().fillPanelFromModel(space);
		getSignalSourceLevelPanel().fillPanelFromModel(space);

	}

	/**
	 * Fills the given {@link SignalSpace} from the dependent panels
	 * ({@link TimeSpacePanel#fillModelFromPanel(SignalSpace) TimeSpacePanel},
	 * {@link SignalSourceLevelPanel#fillModelFromPanel(SignalSpace)
	 * SignalSourceLevelPanel} and
	 * {@link ChannelSpacePanel#fillModelFromPanel(SignalSpace)
	 * ChannelSpacePanel}).
	 * @param space the signal space
	 */
	public void fillModelFromPanel(SignalSpace space) {

		getTimeSpacePanel().fillModelFromPanel(space);
		getChannelSpacePanel().fillModelFromPanel(space);
		getSignalSourceLevelPanel().fillModelFromPanel(space);

	}

	/**
	 * Sets the {@link SignalSpaceConstraints parameters} of the signal
	 * in dependent panels
	 * ({@link TimeSpacePanel#setConstraints(SignalSpaceConstraints)
	 * TimeSpacePanel}
	 * and {@link ChannelSpacePanel#setConstraints(SignalSpaceConstraints)
	 * ChannelSpacePanel}).
	 * @param constraints the parameters of the signal
	 */
	public void setConstraints(SignalSpaceConstraints constraints) {

		getTimeSpacePanel().setConstraints(constraints);
		getChannelSpacePanel().setConstraints(constraints);

	}

	/**
	 * Validates the dependent panels
	 * ({@link TimeSpacePanel#validatePanel(Errors) TimeSpacePanel},
	 * {@link SignalSourceLevelPanel#validatePanel(Errors)
	 * SignalSourceLevelPanel} and
	 * {@link ChannelSpacePanel#validatePanel(Errors)
	 * ChannelSpacePanel}).
	 * @param errors the object in which the errors will be stored
	 */
	public void validatePanel(ValidationErrors errors) {

		getTimeSpacePanel().validatePanel(errors);
		getChannelSpacePanel().validatePanel(errors);
		getSignalSourceLevelPanel().validatePanel(errors);

	}

}
