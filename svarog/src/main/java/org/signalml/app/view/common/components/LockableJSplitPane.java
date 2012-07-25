/* LockableJSplitPane.java created 2007-09-26
 *
 */

package org.signalml.app.view.common.components;

import java.awt.Component;

import javax.swing.JSplitPane;

/**
 * Split pane which can be locked so that the user can not move the divider.
 * <p>
 * As there is no no other way prevent the user from moving the divider, when
 * this panel should be locked, the size of the divider is set to 0 (the last
 * size is stored in the field and restored when this panel is unlocked).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LockableJSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	/**
	 * boolean which say if this split pane is locked
	 */
	private boolean locked = false;
	/**
	 * the stored size of the divider; the variable is used when {@code locked}
	 * mode is active
	 */
	private int lockedDividerSize;

	/**
	 * Creates a new <code>LockableJSplitPane</code> configured to arrange the
	 * child components side-by-side horizontally with no continuous
	 * layout, using two buttons for the components.
	 * The initial state of this panel is {@code unlocked}.
	 */
	public LockableJSplitPane() {
		super();
	}

	/**
	 * Creates a new <code>LockableJSplitPane</code> with the specified
	 * orientation and
	 * redrawing style, and with the specified components.
	 *
	 * @param newOrientation  <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	 *                        <code>JSplitPane.VERTICAL_SPLIT</code>
	 * @param newContinuousLayout  a boolean, true for the components to
	 *        redraw continuously as the divider changes position, false
	 *        to wait until the divider position stops changing to redraw
	 * @param newLeftComponent the <code>Component</code> that will
	 *		appear on the left
	 *        	of a horizontally-split pane, or at the top of a
	 *        	vertically-split pane
	 * @param newRightComponent the <code>Component</code> that will
	 *		appear on the right
	 *        	of a horizontally-split pane, or at the bottom of a
	 *        	vertically-split pane
	 * @exception IllegalArgumentException if <code>orientation</code>
	 *		is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public LockableJSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
	}

	/**
	* Creates a new <code>LockableJSplitPane</code> with the specified
	* orientation and redrawing style.
	*
	* @param newOrientation  <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	*                        <code>JSplitPane.VERTICAL_SPLIT</code>
	* @param newContinuousLayout  a boolean, true for the components to
	*        redraw continuously as the divider changes position, false
	*        to wait until the divider position stops changing to redraw
	* @exception IllegalArgumentException if <code>orientation</code>
	*		is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
	*/
	public LockableJSplitPane(int newOrientation, boolean newContinuousLayout) {
		super(newOrientation, newContinuousLayout);
	}

	/**
	 * Creates a new <code>LockableJSplitPane</code> with the specified
	 * orientation and
	 * with the specified components that do not do continuous
	 * redrawing.
	 *
	 * @param newOrientation  <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	 *                        <code>JSplitPane.VERTICAL_SPLIT</code>
	 * @param newLeftComponent the <code>Component</code> that will
	 *		appear on the left
	 *        	of a horizontally-split pane, or at the top of a
	 *        	vertically-split pane
	 * @param newRightComponent the <code>Component</code> that will
	 *		appear on the right
	 *        	of a horizontally-split pane, or at the bottom of a
	 *        	vertically-split pane
	 * @exception IllegalArgumentException if <code>orientation</code>
	 *		is not one of: HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public LockableJSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
	}

	/**
	 * Creates a new <code>LockableJSplitPane</code> configured with the
	 * specified orientation and no continuous layout.
	 *
	 * @param newOrientation  <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	 *                        <code>JSplitPane.VERTICAL_SPLIT</code>
	 * @exception IllegalArgumentException if <code>orientation</code>
	 *		is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT.
	 */
	public LockableJSplitPane(int newOrientation) {
		super(newOrientation);
	}

	public boolean isLocked() {
		return locked;
	}

	/**
	 * Sets if this split panel should be locked (if the user should be able to
	 * move the divider).
	 *
	 * XXX ugly hack
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
	 * @param locked {@code true} if this panel should be locked (user shouldn't
	 * be able to move the divider), {@code false} otherwise
	 */

	public void setLocked(boolean locked) {
		if (this.locked != locked) {
			if (locked) {
				lockedDividerSize = getDividerSizeInternal();
			}
			setDividerSizeInternal(locked ? 0 : lockedDividerSize);
			this.locked = locked;
		}
	}

	/**
	 * Returns the current real size of the divider.
	 * @see JSplitPane#getDividerSize()
	 * @return the current real size of the divider
	 */
	private int getDividerSizeInternal() {
		return super.getDividerSize();
	}

	/**
	 * Sets the current real size of the divider.
	 * @see JSplitPane#setDividerSize(int)
	 * @param newSize the size to set
	 */
	private void setDividerSizeInternal(int newSize) {
		super.setDividerSize(newSize);
	}

	/**
	 * Sets the size of the divider:
	 * <ul>
	 * <li>if this panel is locked stores the size in the field {@code
	 * lockedDividerSize} and uses it when the panel will be unlocked,</li>
	 * <li>otherwise sets the size of the divider -
	 * {@link JSplitPane#setDividerSize(int)}</li></ul>
	 */
	@Override
	public void setDividerSize(int newSize) {
		if (locked) {
			lockedDividerSize = newSize;
		} else {
			super.setDividerSize(newSize);
		}
	}

	/**
	 * Returns the size of the divider:
	 * <ul>
	 * <li>if the panel is locked the size stored in the field {@code
	 * lockedDividerSize},</li>
	 * <li>otherwise the real size of the divider -
	 * {@link JSplitPane#setDividerSize(int)}</li></ul>
	 */
	@Override
	public int getDividerSize() {
		return (locked) ? lockedDividerSize : super.getDividerSize();
	}



}
