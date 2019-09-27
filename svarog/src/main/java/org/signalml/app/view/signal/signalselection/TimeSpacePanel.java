/* TimeSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.signal.signalselection;

import com.alee.laf.tabbedpane.WebTabbedPane;
import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.domain.signal.space.MarkerTimeSpace;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.TagStyle;

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

	public static final int WHOLE_SIGNAL_TAB_INDEX = 0;
	public static final int SIGNAL_SELECTION_TAB_INDEX = 1;
	public static final int MARKER_SELECTION_TAB_INDEX = 2;

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
	protected JTabbedPane tabbedPane;

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
			selectedTimeSpacePanel = new SignalSelectionPanel(false);
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
			tabbedPane = new WebTabbedPane(WebTabbedPane.TOP);

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

		enableTabsAsNeeded(space);

	}

	protected void enableTabsAsNeeded(SignalSpace space) {

		TimeSpaceType timeSpaceType = space.getTimeSpaceType();
		JTabbedPane tPane = getTabbedPane();

		switch (timeSpaceType) {

		case MARKER_BASED :
			if (tPane.isEnabledAt(MARKER_SELECTION_TAB_INDEX)) {
				tPane.setSelectedIndex(MARKER_SELECTION_TAB_INDEX);
			} else {
				tPane.setSelectedIndex(WHOLE_SIGNAL_TAB_INDEX);
			}
			break;

		case SELECTION_BASED :
			tPane.setSelectedIndex(SIGNAL_SELECTION_TAB_INDEX);
			break;

		case WHOLE_SIGNAL :
		default :
			tPane.setSelectedIndex(WHOLE_SIGNAL_TAB_INDEX);
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

		case MARKER_SELECTION_TAB_INDEX :
			space.setTimeSpaceType(TimeSpaceType.MARKER_BASED);
			getMarkedTimeSpacePanel().fillModelFromPanel(space);
			space.setSelectionTimeSpace(null);
			break;

		case SIGNAL_SELECTION_TAB_INDEX :
			space.setTimeSpaceType(TimeSpaceType.SELECTION_BASED);
			getSelectedTimeSpacePanel().fillModelFromPanel(space);
			space.setMarkerTimeSpace(null);
			break;

		case WHOLE_SIGNAL_TAB_INDEX :
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
		getMarkedTimeSpacePanel().setConstraints(constraints);

		JTabbedPane tabbedPane = getTabbedPane();

		TagStyle[] markerStyles = constraints.getMarkerStyles();
		if (markerStyles != null && markerStyles.length > 0) {
			setEnabled(MARKER_SELECTION_TAB_INDEX, true);
		} else {
			if (tabbedPane.getSelectedIndex() == MARKER_SELECTION_TAB_INDEX) {
				tabbedPane.setSelectedIndex(WHOLE_SIGNAL_TAB_INDEX);
			}
			setEnabled(MARKER_SELECTION_TAB_INDEX, false);
		}

	}

	protected void setEnabled(int index, boolean enabled) {
		if (getTabbedPane().getTabCount() > index)
			tabbedPane.setEnabledAt(index, enabled);
	}

	/**
	 * Validates this panel.
	 * This panel is valid if the currently selected tab is valid.
	 * @param errors the variable in which errors are stored
	 */
	public void validatePanel(ValidationErrors errors) {

		int index = getTabbedPane().getSelectedIndex();

		switch (index) {

		case MARKER_SELECTION_TAB_INDEX :
			getMarkedTimeSpacePanel().validatePanel(errors);
			break;

		case SIGNAL_SELECTION_TAB_INDEX :
			getSelectedTimeSpacePanel().validatePanel(errors);
			break;

		case WHOLE_SIGNAL_TAB_INDEX :
		default :
			// do nothing
			break;

		}

	}

}
