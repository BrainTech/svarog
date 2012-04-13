/* ViewerTabbedPane.java created 2007-09-11
 *
 */
package org.signalml.app.view.workspace;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;

/** ViewerTabbedPane
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ViewerTabbedPane.class);

	public ViewerTabbedPane(int tabLocation, int tabLayout) {
		super(tabLocation, tabLayout);
		setBorder(new EmptyBorder(3,3,3,3));
	}

	public ViewerTabbedPane() {
		this(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	/**
	 * Adds a new tab to this pane.
	 *
	 * Note: this method has changed on 2011-11-05. Now title and toolTip parameters
	 * should be strings to display, NOT their keys! Message keys are now obsolete.
	 *
	 * @param title tab title
	 * @param iconPath
	 * @param component
	 * @param toolTip tooltip text
	 */
	public void addTab(String title, String iconPath, Component component, String toolTip) {
		Icon icon = null;
		if (iconPath != null)
			icon = IconUtils.loadClassPathIcon(iconPath);
		addTab(title,icon,component,toolTip);
	}
}
