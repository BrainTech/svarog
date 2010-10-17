/* SignalTool.java created 2007-09-26
 *
 */

package org.signalml.plugin.export.signal;

import java.awt.Cursor;

import javax.swing.event.MouseInputAdapter;

import org.signalml.app.view.signal.SignalView;

/**
 * Abstract implementation of {@link SignalTool} interface.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author STF &copy; 2010 eisenbits
 */
public abstract class AbstractSignalTool extends MouseInputAdapter implements SignalTool {

    /** {@link SignalView} this SignalTool is associated with. */
	private SignalView signalView;
	/** Tells whether this SignalTool is currently processing any mouse event sequence. */
	private boolean engaged = false;

	/**
	 * Constructs a new empty SignalTool.
	 */
	protected AbstractSignalTool(){
		
	}
	
	/**
	 * Constructs a new SignalTool and associates it with the specified {@link SignalView}.
	 * 
	 * @param signalView {@link SignalView} this signal tool is to be associated with.
	 */
	protected AbstractSignalTool(SignalView signalView) {
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
	 * Returns the {@link SignalView} associated with this SignalTool.
	 *
	 * @return {@link #signalView}
	 */
	protected SignalView getSignalView() {
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
	public void setSignalView(SignalView signalView){
		this.signalView = signalView;
	}
	
	@Override
	public SignalTool createCopy() throws UnsupportedOperationException{
		throw new UnsupportedOperationException("operation not supported");
	}
}
