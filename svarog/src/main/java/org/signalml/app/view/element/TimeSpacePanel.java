/* TimeSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.space.MarkerTimeSpace;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.TagStyle;

import org.springframework.validation.Errors;

/**
 * Panel which allows to select the time fragment of the signal:
 * <ul>
 * <li>the whole signal - {@link #getWholeTimeSpacePanel()},</li>
 * <li>the fragment selected with the signal selection - {@link
 * #getSelectedTimeSpacePanel()},</li>
 * <li>the part of the signal in the neighborhood of the markers
 * - {@link #getMarkedTimeSpacePanel()}.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TimeSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(TimeSpacePanel.class);

	/**
	 * the tabbed pane with 3 tabs which allow to select the part of the
	 * signal (in the time domain):
	 * <ul>
	 * <li>the whole signal - {@link #wholeTimeSpacePanel},</li>
	 * <li>the selected part of the signal selection - {@link
	 * #selectedTimeSpacePanel},</li>
	 * <li>the part of the signal in the neighborhood of the markers
	 * - {@link #markedTimeSpacePanel},</li>
	 * </ul>
	 */
	private JTabbedPane tabbedPane;

	/**
	 * the {@link WholeTimeSpacePanel panel} which allows to select the whole
	 * signal and displays the parameters of this signal
	 */
	private WholeTimeSpacePanel wholeTimeSpacePanel;
	/**
	 * the {@link SignalSelectionPanel panel} which allows to take the selected
	 * part of the signal
	 */
	private SignalSelectionPanel selectedTimeSpacePanel;
	/**
	 * the {@link MarkedTimeSpacePanel panel} which allows to select the part
	 * of the signal in the neighborhood of the markers
	 */
	private MarkedTimeSpacePanel markedTimeSpacePanel;

	/**
	 * Constructor. Initializes the panel.
	 */
	public TimeSpacePanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with BorderLayout and the {@link #getTabbedPane()
	 * tabbed pane}.
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		setBorder(new CompoundBorder(
		                  new TitledBorder(_("Time fragment selection")),
		                  new EmptyBorder(3,3,3,3)
		          ));

		add(getTabbedPane());

	}

	/**
	 * Returns the {@link WholeTimeSpacePanel panel} which allows to select
	 * the whole signal and displays the parameters of this signal.
	 * If the panel doesn't exist it is created.
	 * @return the panel which allows to select the whole signal and displays
	 * the parameters of this signal
	 */
	public WholeTimeSpacePanel getWholeTimeSpacePanel() {
		if (wholeTimeSpacePanel == null) {
			wholeTimeSpacePanel = new WholeTimeSpacePanel();
		}
		return wholeTimeSpacePanel;
	}

	/**
	 * Returns the {@link SignalSelectionPanel panel} which allows to take the
	 * selected part of the signal.
	 * If the panel doesn't exist it is created.
	 * @return the panel which allows to take the selected part of the signal
	 */
	public SignalSelectionPanel getSelectedTimeSpacePanel() {
		if (selectedTimeSpacePanel == null) {
			selectedTimeSpacePanel = new SignalSelectionPanel( false);
		}
		return selectedTimeSpacePanel;
	}

	/**
	 * Returns the {@link MarkedTimeSpacePanel panel} which allows to select
	 * the part of the signal in the neighborhood of the markers.
	 * If the panel doesn't exist it is created.
	 * @return the panel which allows to select the part of the signal in
	 * the neighborhood of the markers
	 */
	public MarkedTimeSpacePanel getMarkedTimeSpacePanel() {
		if (markedTimeSpacePanel == null) {
			markedTimeSpacePanel = new MarkedTimeSpacePanel();
		}
		return markedTimeSpacePanel;
	}

	/**
	 * Returns the tabbed pane with 3 tabs which allow to select the part of
	 * the signal (in the time domain):
	 * <ul>
	 * <li>the whole signal - {@link #getWholeTimeSpacePanel()},</li>
	 * <li>the selected part of the signal selection - {@link
	 * #getSelectedTimeSpacePanel()},</li>
	 * <li>the part of the signal in the neihgbourhood of the markers
	 * - {@link #getMarkedTimeSpacePanel()}.</li>
	 * </ul>
	 * If the panel doesn't exist it is created.
	 * @return the tabbed pane which allow to select the part of the signal
	 */
	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

			tabbedPane.addTab(_("Whole signal"), getWholeTimeSpacePanel());
			tabbedPane.addTab(_("Selected"), getSelectedTimeSpacePanel());
			tabbedPane.addTab(_("Marked"), getMarkedTimeSpacePanel());

		}
		return tabbedPane;
	}

	/**
	 * Fills the fields of this panel using the given {@link SignalSpace model}:
	 * <ul>
	 * <li>fills the sub-panels:
	 * <ul><li>the {@link WholeTimeSpacePanel#fillPanelFromModel(SignalSpace)
	 * WholeTimeSpacePanel},</li>
	 * <li>the {@link SignalSelectionPanel#fillPanelFromModel(SignalSpace)
	 * SignalSelectionPanel},</li>
	 * <li>the {@link MarkedTimeSpacePanel#fillPanelFromModel(SignalSpace)
	 * MarkedTimeSpacePanel},</li></ul></li>
	 * <li>depending on the {@link TimeSpaceType type} of the time space
	 * activates the corresponding tab in the {@link #getTabbedPane() tabbed
	 * pane}.</li>
	 * </ul>
	 * @param space the model
	 */
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

	/**
	 * Stores the user input in the {@link SignalSpace model}.
	 * Depending on the currently selected tab in the {@link #getTabbedPane()
	 * tabbed pane}:
	 * <ul>
	 * <li>If it is the tab for the {@link TimeSpaceType#WHOLE_SIGNAL whole
	 * signal}:
	 * <ul>
	 * <li> {@link WholeTimeSpacePanel#fillModelFromPanel(SignalSpace) fills}
	 * the model from {@link WholeTimeSpacePanel this panel},</li>
	 * <li>sets that there is no {@link SignalSelection selection} or
	 * {@link MarkerTimeSpace marker} time space.</li></ul></li>
	 * <li>If it is the tab {@link TimeSpaceType#SELECTION_BASED based on the
	 * selection}:
	 * <ul>
	 * <li> {@link SignalSelectionPanel#fillModelFromPanel(SignalSpace) fills}
	 * the model from {@link SignalSelectionPanel this panel},</li>
	 * <li>sets that there is no {@link MarkerTimeSpace marker time space}.
	 * </li></ul></li>
	 * <li>If it is the tab {@link TimeSpaceType#MARKER_BASED based on the
	 * marker}:
	 * <ul>
	 * <li> {@link MarkedTimeSpacePanel#fillModelFromPanel(SignalSpace) fills}
	 * the model from {@link MarkedTimeSpacePanel this panel},</li>
	 * <li>sets that there is no {@link SignalSelection selection time space}.
	 * </li></ul></li></ul>
	 * @param space the model
	 */
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

	/**
	 * Sets the {@link SignalSpaceConstraints parameters} of the signal in the
	 * panels:
	 * <ul>
	 * <li>the {@link WholeTimeSpacePanel#setConstraints(SignalSpaceConstraints)
	 * WholeTimeSpacePanel},</li>
	 * <li>the {@link SignalSelectionPanel#setConstraints(SignalSpaceConstraints)
	 * SignalSelectionPanel},</li>
	 * <li>the {@link MarkedTimeSpacePanel#setConstraints(SignalSpaceConstraints)
	 * MarkedTimeSpacePanel}.</li>
	 * If the {@link SignalSpaceConstraints#getMarkerStyles() list of marker
	 * styles} contains no elements disables the corresponding tab, otherwise
	 * enables it.
	 * @param constraints the parameters of the signal
	 */
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

	/**
	 * Validates this panel.
	 * This panel is valid if the currently selected tab is valid.
	 * @param errors the variable in which errors are stored
	 */
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
