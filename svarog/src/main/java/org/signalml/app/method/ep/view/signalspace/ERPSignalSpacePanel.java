package org.signalml.app.method.ep.view.signalspace;

import org.signalml.app.view.signal.signalselection.SignalSpacePanel;
import org.signalml.app.view.signal.signalselection.TimeSpacePanel;

public class ERPSignalSpacePanel extends SignalSpacePanel {

	@Override
	public TimeSpacePanel getTimeSpacePanel() {
		if (timeSpacePanel == null) {
			timeSpacePanel = new ERPTimeSpacePanel();
			((ERPTimeSpacePanel) timeSpacePanel).hideMarkerTab();
		}
		return timeSpacePanel;
	}
}
