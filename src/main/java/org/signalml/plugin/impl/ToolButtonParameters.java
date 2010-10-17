/**
 * 
 */
package org.signalml.plugin.impl;

import java.awt.event.MouseListener;

import javax.swing.Icon;

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
	private MouseListener listener;
	
	/**
	 * Constructor. Sets parameters.
	 * @param toolTipText the text used in tooltip
	 * @param icon the icon of the button
	 * @param listener the listener for mouse events
	 */
	public ToolButtonParameters(String toolTipText, Icon icon, MouseListener listener) {
		this.toolTipText = toolTipText;
		this.icon = icon;
		this.listener = listener;
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
	public MouseListener getListener() {
		return listener;
	}
}
