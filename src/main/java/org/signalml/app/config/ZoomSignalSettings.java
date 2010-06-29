/* ZoomSignalSettings.java created 2007-12-17
 *
 */

package org.signalml.app.config;

import java.awt.Dimension;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ZoomSignalSettings
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("zoomsettings")
public class ZoomSignalSettings {

	private boolean channelSwitching;
	private Dimension zoomSize;
	private float factor;

	public ZoomSignalSettings() {
		channelSwitching = true;
		zoomSize = new Dimension(200,200);
		factor = 2F;
	}

	public boolean isChannelSwitching() {
		return channelSwitching;
	}

	public void setChannelSwitching(boolean channelSwitching) {
		this.channelSwitching = channelSwitching;
	}

	public Dimension getZoomSize() {
		return zoomSize;
	}

	public void setZoomSize(Dimension zoomSize) {
		this.zoomSize = zoomSize;
	}

	public float getFactor() {
		return factor;
	}

	public void setFactor(float factor) {
		this.factor = factor;
	}

}
