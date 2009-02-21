/* MainFrameConfiguration.java created 2007-09-19
 * 
 */

package org.signalml.app.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MainFrameConfiguration
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mainframe")
public class MainFrameConfiguration extends AbstractXMLConfiguration {

	private boolean maximized;
	private int xSize;
	private int ySize;
	
	private boolean mainToolBarVisible;
	private boolean leftPanelVisible;
	private boolean bottomPanelVisible;
	private boolean statusBarVisible;
	
	@XStreamAlias("documentMaximized")
	private boolean viewMode;
	
	private int hDividerLocation;
	private int vDividerLocation;
	
	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	public int getXSize() {
		return xSize;
	}

	public void setXSize(int size) {
		xSize = size;
	}

	public int getYSize() {
		return ySize;
	}

	public void setYSize(int size) {
		ySize = size;
	}
	
	public boolean isViewMode() {
		return viewMode;
	}

	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
	}

	public int getHDividerLocation() {
		return hDividerLocation;
	}

	public void setHDividerLocation(int dividerLocation) {
		hDividerLocation = dividerLocation;
	}

	public int getVDividerLocation() {
		return vDividerLocation;
	}

	public void setVDividerLocation(int dividerLocation) {
		vDividerLocation = dividerLocation;
	}

	
	public boolean isMainToolBarVisible() {
		return mainToolBarVisible;
	}

	public void setMainToolBarVisible(boolean mainToolBarVisible) {
		this.mainToolBarVisible = mainToolBarVisible;
	}

	public boolean isLeftPanelVisible() {
		return leftPanelVisible;
	}

	public void setLeftPanelVisible(boolean leftPanelVisible) {
		this.leftPanelVisible = leftPanelVisible;
	}

	public boolean isBottomPanelVisible() {
		return bottomPanelVisible;
	}

	public void setBottomPanelVisible(boolean bottomPanelVisible) {
		this.bottomPanelVisible = bottomPanelVisible;
	}

	public boolean isStatusBarVisible() {
		return statusBarVisible;
	}

	public void setStatusBarVisible(boolean statusBarVisible) {
		this.statusBarVisible = statusBarVisible;
	}

	@Override
	public String getStandardFilename() {
		return "mainframe-config.xml";
	}

}
