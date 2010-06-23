/* ViewerTabbedPane.java created 2007-09-11
 * 
 */
package org.signalml.app.view;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import org.springframework.context.support.MessageSourceAccessor;

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

	public void addTab(String title, String iconPath, Component component, String toolTip, MessageSourceAccessor messageSource) {
		Icon icon = null;
		String toolTipText = null;
		if( iconPath != null ) {
			icon = IconUtils.loadClassPathIcon(iconPath);
		}
		if( toolTip != null ) {
			toolTipText = messageSource.getMessage(toolTip);
		}
		title = messageSource.getMessage(title);
		
		addTab(title,icon,component,toolTipText);			
	}
	
}
