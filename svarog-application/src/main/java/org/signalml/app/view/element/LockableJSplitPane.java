/* LockableJSplitPane.java created 2007-09-26
 * 
 */

package org.signalml.app.view.element;

import java.awt.Component;

import javax.swing.JSplitPane;

/** LockableJSplitPane
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LockableJSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	private boolean locked = false;
	private int lockedDividerSize;

	public LockableJSplitPane() {
		super();
	}

	public LockableJSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
	}

	public LockableJSplitPane(int newOrientation, boolean newContinuousLayout) {
		super(newOrientation, newContinuousLayout);
	}

	public LockableJSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
	}

	public LockableJSplitPane(int newOrientation) {
		super(newOrientation);
	}

	public boolean isLocked() {
		return locked;
	}

	/* XXX ugly hack
	 * 
	 * As of Swing/Java 6.0 there seems to be no other way prevent the user from moving the divider.
	 * Setting the whole split pane to disabled achieves the effect, but breaks other things,
	 * for instance cursors are not changed for any child components.
	 * 
	 * The problem is mentioned in several bugs (7+ years old) and forum threads, 
	 * with no better solution given.
	 * 
	 * The methods below achieve an acceptable effect via resizing the divider to zero.
	 * Underlying divider size management methods are wrapped to achieve separation
	 * of those two properties.
	 */
	
	public void setLocked(boolean locked) {
		if( this.locked != locked ) {			
			if( locked ) {
				lockedDividerSize = getDividerSizeInternal();
			}
			setDividerSizeInternal( locked ? 0 : lockedDividerSize );
			this.locked = locked;
		}
	}

	private int getDividerSizeInternal() {
		return super.getDividerSize();
	}

	private void setDividerSizeInternal(int newSize) {
		super.setDividerSize(newSize);
	}
	
	@Override
	public void setDividerSize(int newSize) {
		if( locked ) {
			lockedDividerSize = newSize;
		} else {
			super.setDividerSize(newSize);
		}
	}
	
	@Override
	public int getDividerSize() {
		return ( locked ) ? lockedDividerSize : super.getDividerSize();
	}
	
	
		
}
