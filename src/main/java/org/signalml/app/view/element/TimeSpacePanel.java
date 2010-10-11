/* TimeSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** TimeSpacePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TimeSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(TimeSpacePanel.class);

	private MessageSourceAccessor messageSource;

	private JTabbedPane tabbedPane;

	private WholeTimeSpacePanel wholeTimeSpacePanel;
	private SignalSelectionPanel selectedTimeSpacePanel;
	private MarkedTimeSpacePanel markedTimeSpacePanel;

	public TimeSpacePanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		setBorder(new CompoundBorder(
		                  new TitledBorder(messageSource.getMessage("signalSpace.timeSpace.title")),
		                  new EmptyBorder(3,3,3,3)
		          ));

		add(getTabbedPane());

	}

	public WholeTimeSpacePanel getWholeTimeSpacePanel() {
		if (wholeTimeSpacePanel == null) {
			wholeTimeSpacePanel = new WholeTimeSpacePanel(messageSource);
		}
		return wholeTimeSpacePanel;
	}

	public SignalSelectionPanel getSelectedTimeSpacePanel() {
		if (selectedTimeSpacePanel == null) {
			selectedTimeSpacePanel = new SignalSelectionPanel(messageSource, false);
		}
		return selectedTimeSpacePanel;
	}

	public MarkedTimeSpacePanel getMarkedTimeSpacePanel() {
		if (markedTimeSpacePanel == null) {
			markedTimeSpacePanel = new MarkedTimeSpacePanel(messageSource);
		}
		return markedTimeSpacePanel;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

			tabbedPane.addTab(messageSource.getMessage("signalSpace.timeSpace.wholeSignal"), getWholeTimeSpacePanel());
			tabbedPane.addTab(messageSource.getMessage("signalSpace.timeSpace.selectedSignal"), getSelectedTimeSpacePanel());
			tabbedPane.addTab(messageSource.getMessage("signalSpace.timeSpace.markedSignal"), getMarkedTimeSpacePanel());

		}
		return tabbedPane;
	}

	public void fillPanelFromModel(SignalSpace space) {

		getWholeTimeSpacePanel().fillPanelFromModel(space);
		getSelectedTimeSpacePanel().fillPanelFromModel(space);
		getMarkedTimeSpacePanel().fillPanelFromModel(space);

		TimeSpaceType timeSpaceType = space.getTimeSpaceType();

		JTabbedPane tPane = getTabbedPane();

		switch (timeSpaceType) {

		case MARKER_BASED :
			if (tPane.isEnabledAt(2)) {
				tPane.setSelectedIndex(2);
			} else {
				tPane.setSelectedIndex(0);
			}
			break;

		case SELECTION_BASED :
			tPane.setSelectedIndex(1);
			break;

		case WHOLE_SIGNAL :
		default :
			tPane.setSelectedIndex(0);
			break;

		}

	}

	public void fillModelFromPanel(SignalSpace space) {

		int index = getTabbedPane().getSelectedIndex();

		switch (index) {

		case 2 :
			space.setTimeSpaceType(TimeSpaceType.MARKER_BASED);
			getMarkedTimeSpacePanel().fillModelFromPanel(space);
			space.setSelectionTimeSpace(null);
			break;

		case 1 :
			space.setTimeSpaceType(TimeSpaceType.SELECTION_BASED);
			getSelectedTimeSpacePanel().fillModelFromPanel(space);
			space.setMarkerTimeSpace(null);
			break;

		case 0 :
		default :
			space.setTimeSpaceType(TimeSpaceType.WHOLE_SIGNAL);
			getWholeTimeSpacePanel().fillModelFromPanel(space);
			space.setSelectionTimeSpace(null);
			space.setMarkerTimeSpace(null);
			break;

		}

	}

	public void setConstraints(SignalSpaceConstraints constraints) {

		getWholeTimeSpacePanel().setConstraints(constraints);
		getSelectedTimeSpacePanel().setConstraints(constraints);
		MarkedTimeSpacePanel markerPanel = getMarkedTimeSpacePanel();

		JTabbedPane tabbedPane = getTabbedPane();

		markerPanel.setConstraints(constraints);

		TagStyle[] markerStyles = constraints.getMarkerStyles();
		if (markerStyles != null && markerStyles.length > 0) {
			tabbedPane.setEnabledAt(2, true);
		} else {
			if (tabbedPane.getSelectedIndex() == 2) {
				tabbedPane.setSelectedIndex(0);
			}
			tabbedPane.setEnabledAt(2, false);
		}

	}

	public void validatePanel(Errors errors) {

		int index = getTabbedPane().getSelectedIndex();

		switch (index) {

		case 2 :
			getMarkedTimeSpacePanel().validatePanel(errors);
			break;

		case 1 :
			getSelectedTimeSpacePanel().validatePanel(errors);
			break;

		case 0 :
		default :
			// do nothing
			break;

		}

	}

}
