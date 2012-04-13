package org.signalml.plugin.export.signal;

import java.awt.Cursor;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputListener;

import org.signalml.app.view.signal.SignalView;
import org.signalml.plugin.export.view.ExportedSignalView;

/**
 * Mouse event processor associated with a single {@link ExportedSignalView} instance.
 *
 * @author STF &copy; 2010 eisenbits
 */
public interface SignalTool extends MouseInputListener, MouseWheelListener {

	/**
	 * Tells if this SignalTool is processing a mouse event sequence.
	 * <p>
	 * Mouse actions are often composed of multiple events, for instance:
	 * {@link MouseInputListener#mousePressed(java.awt.event.MouseEvent)},
	 * {@link MouseInputListener#mouseDragged(java.awt.event.MouseEvent)},
	 * {@link MouseInputListener#mouseReleased(java.awt.event.MouseEvent)}.
	 * Recognized sequences and their semantics are implementation dependent.
	 * <p>
	 * The value returned by this method serves as an indicator that this SignalTool
	 * is currently involved in processing some mouse action and that other mouse
	 * triggered actions associated with the same {@link SignalView} instance (like
	 * popup menu) should be blocked. This helps to avoid interference.
	 * <p>
	 * A sample implementation can store a boolean variable and set it to true in
	 * {@link MouseInputListener#mousePressed(java.awt.event.MouseEvent)} and to false
	 * in {@link MouseInputListener#mouseReleased(java.awt.event.MouseEvent)}.
	 *
	 * @return True if this SignalTool is in the middle of some mouse event sequence processing,
	 * and false otherwise.
	 */
	boolean isEngaged();

	/**
	 * Tells if this SignalTool supports column header.
	 * <p>
	 * TODO better description
	 *
	 * @return True if this SignalTool supports column header, and false otherwise.
	 */
	boolean supportsColumnHeader();

	/**
	 * Tells if this SignalTool supports row header.
	 * <p>
	 * TODO better description
	 *
	 * @return True if this SignalTool supports row header, and false otherwise.
	 */
	boolean supportsRowHeader();

	/**
	 * Returns mouse cursor bitmap to use with this SignalTool.
	 *
	 * @return mouse cursor bitmap to use with this SignalTool.
	 */
	Cursor getDefaultCursor();

	/**
	 * Sets the associated {@link ExportedSignalView signal view}.
	 * @param signalView the associated signal view
	 */
	void setSignalView(ExportedSignalView signalView);

	/**
	 * Creates a copy of this signal tool if such operation is supported
	 * by implementation.
	 * If the implementations doesn't support it exception is thrown.
	 * @return the created copy
	 * @throws UnsupportedOperationException if the implementation doesn't
	 * support the creation of a copy
	 */
	SignalTool createCopy() throws UnsupportedOperationException;
}
