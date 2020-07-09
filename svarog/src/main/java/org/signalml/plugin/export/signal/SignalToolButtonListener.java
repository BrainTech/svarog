package org.signalml.plugin.export.signal;

import java.awt.event.MouseListener;
import javax.swing.JToggleButton;
import org.signalml.plugin.export.view.SvarogAccessGUI;

/**
 * Interface for a listener on a {@code JToggleButton} associated with the
 * {@link SignalTool}.
 * This interface must be implemented if the plug-in wants to pass the listener
 * to function {@link SvarogAccessGUI#addSignalTool(SignalTool,
 * javax.swing.Icon, String, SignalToolButtonListener)}.
 * <p>
 * Usually such listers are used to open the dialog with the parameters of the
 * tool.
 *
 * @author Marcin Szumski
 */
public interface SignalToolButtonListener extends MouseListener {

	/**
	 * Creates the copy of this listener with the {@link SignalTool tool} for
	 * which it should be created and the button on which it will listen.
	 * @param tool the tool with which this listener should be associated
	 * @param button the button on which this listener will listen
	 * @return the created copy
	 */
	SignalToolButtonListener createCopy(SignalTool tool, JToggleButton button);

}
