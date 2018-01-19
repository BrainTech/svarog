package org.signalml.plugin.psychopy;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.impl.ToolButtonParameters;

import javax.swing.*;

public class PsychopyPlugin implements Plugin {
	@Override
	public void register(SvarogAccess access) throws Exception {
		SvarogAccessGUI gui = access.getGUIAccess();
		ImageIcon psychopyIcon = new ImageIcon(
			getClass().getResource("icon/psychopy.png"),
			"Psychopy logo"
		);
		ToolButtonParameters parameters = new ToolButtonParameters(
				"Psychopy",
				psychopyIcon,
				null
		);
		gui.addSignalTool(new PsychopyTool(), parameters);
	}
}
