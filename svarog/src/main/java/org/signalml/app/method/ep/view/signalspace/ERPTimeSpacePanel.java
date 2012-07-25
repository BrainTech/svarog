package org.signalml.app.method.ep.view.signalspace;

import org.signalml.app.view.signal.signalselection.TimeSpacePanel;
import org.signalml.domain.signal.space.SignalSpace;

public class ERPTimeSpacePanel extends TimeSpacePanel {

	public void hideMarkerTab() {
		tabbedPane.remove(getMarkedTimeSpacePanel());
	}

	@Override
	public void fillPanelFromModel(SignalSpace space) {
		// TODO Auto-generated method stub
		//super.fillPanelFromModel(space);
	}
}
