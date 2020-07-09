/* SignalTool.java created 2007-09-26
 *
 */

package org.signalml.plugin.export.signal;

import java.awt.Cursor;
import javax.swing.event.MouseInputAdapter;
import org.signalml.plugin.export.view.ExportedSignalView;

/**
 * Abstract implementation of {@link SignalTool} interface.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author STF &copy; 2010 eisenbits
 */
public abstract class AbstractSignalTool extends MouseInputAdapter implements SignalTool {

	/** {@link ExportedSignalView} this SignalTool is associated with. */
	private ExportedSignalView signalView;
	/** Tells whether this SignalTool is currently processing any mouse event sequence. */
	private boolean engaged = false;

	/**
	 * Constructs a new empty SignalTool.
	 */
	protected AbstractSignalTool() {

	}

	/**
	 * Constructs a new SignalTool and associates it with the specified {@link
	 * ExportedSignalView SignalView}.
	 * @param signalView {@link ExportedSignalView signal view} this signal
	 * tool is to be associated with
	 */
	protected AbstractSignalTool(ExportedSignalView signalView) {
		super();
		this.signalView = signalView;
	}

	@Override
	public abstract Cursor getDefaultCursor();

	/**
	 * Returns false.
	 *
	 * @return false
	 */
	@Override
	public boolean supportsColumnHeader() {
		return false;
	}

	/**
	 * Returns false.
	 *
	 * @return false
	 */
	@Override
	public boolean supportsRowHeader() {
		return false;
	}

	/**
	 * Returns the {@link ExportedSignalView} associated with this SignalTool.
	 *
	 * @return {@link #signalView}
	 */
	protected ExportedSignalView getSignalView() {
		return signalView;
	}

	/**
	 * Tells whether this SignalTool is currently processing any mouse event sequence.
	 *
	 * @return {@link #engaged}
	 */
	@Override
	public boolean isEngaged() {
		return engaged;
	}

	/**
	 * Sets mouse event sequence processing flag to the given value.
	 *
	 * @param b - new {@link #engaged} value
	 */
	protected void setEngaged(boolean b) {
		this.engaged = b;
	}

	@Override
	public void setSignalView(ExportedSignalView signalView) {
		this.signalView = signalView;
	}

	@Override
	public SignalTool createCopy() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("operation not supported");
	}
}
