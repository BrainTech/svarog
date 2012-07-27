package org.signalml.app.method.ep.view.signalspace;

import org.signalml.app.view.signal.signalselection.TimeSpacePanel;

public class ERPTimeSpacePanel extends TimeSpacePanel {

	public void hideMarkerTab() {
		tabbedPane.remove(getMarkedTimeSpacePanel());
	}

}
