/**
 *
 */
package org.signalml.plugin.impl;

import javax.swing.Icon;

import org.signalml.plugin.export.signal.SignalToolButtonListener;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * Contains the parameters of a button:
 * <ul>
 * <li>icon</li>
 * <li>mouse listener</li>
 * <li>the text used in tooltip</li>
 * </ul>
 * @author Marcin Szumski
 */
public class ToolButtonParameters {
	/**
	 * the text used in tooltip
	 */
	private String toolTipText;
	/**
	 * the icon of the button
	 */
	private Icon icon;
	/**
	 * the listener for mouse events
	 */
	private SignalToolButtonListener listener;

	/**
	 * What happens if the user clicks with the right button on the tool.
	 */
	private AbstractSignalMLAction rightButtonClickAction;

	/**
	 * Constructor. Sets parameters.
	 * @param toolTipText the text used in tooltip
	 * @param icon the icon of the button
	 * @param listener the listener for mouse events
	 */
	public ToolButtonParameters(String toolTipText, Icon icon, SignalToolButtonListener listener, AbstractSignalMLAction rightButtonClickAction) {
		this.toolTipText = toolTipText;
		this.icon = icon;
		this.listener = listener;
		this.rightButtonClickAction = rightButtonClickAction;
	}

	/**
	 * Constructor. Sets parameters.
	 * @param toolTipText the text used in tooltip
	 * @param icon the icon of the button
	 * @param listener the listener for mouse events
	 */
	public ToolButtonParameters(String toolTipText, Icon icon, SignalToolButtonListener listener) {
		this(toolTipText, icon, listener, null);
	}

	/**
	 * @return the text used in tooltip
	 */
	public String getToolTipText() {
		return toolTipText;
	}

	/**
	 * @return the icon of the button
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @return the listener for mouse events
	 */
	public SignalToolButtonListener getListener() {
		return listener;
	}

	public AbstractSignalMLAction getRightButtonClickAction() {
		return rightButtonClickAction;
	}
}
