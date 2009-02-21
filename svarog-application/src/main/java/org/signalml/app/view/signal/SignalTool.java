/* SignalTool.java created 2007-09-26
 * 
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;

import javax.swing.event.MouseInputAdapter;

/** SignalTool
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SignalTool extends MouseInputAdapter {

	protected SignalView signalView;
	protected boolean engaged = false;
	
	public SignalTool( SignalView signalView ) {
		super();
		this.signalView = signalView;
	}

	public SignalView getSignalView() {
		return signalView;
	}
	
	public abstract Cursor getDefaultCursor();

	public boolean supportsColumnHeader() {
		return false;
	}
	
	public boolean supportsRowHeader() {
		return false;
	}
	
	public boolean isEngaged() {
		return engaged;
	}
	
	public final boolean isSignalSelectionTool() {
		return (this instanceof SelectionSignalTool);
	}
	
	public final boolean isSignalTaggingTool() {
		return (this instanceof TaggingSignalTool);
	}
			
}
