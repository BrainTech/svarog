package org.signalml.plugin.psychopy;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.AbstractSignalTool;
import org.signalml.plugin.psychopy.ui.StartPsychopyExperimentDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PsychopyTool extends AbstractSignalTool {
	private StartPsychopyExperimentDialog dialog;

	public PsychopyTool() {
		dialog = new StartPsychopyExperimentDialog();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			dialog.pack();
			dialog.setVisible(true);
			event.consume();
		}
	}

	@Override
	public Cursor getDefaultCursor()  {
		return IconUtils.getCrosshairCursor();
	}

}
