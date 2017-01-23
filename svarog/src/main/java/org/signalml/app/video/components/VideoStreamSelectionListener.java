package org.signalml.app.video.components;

import org.signalml.app.video.VideoStreamSpecification;

/**
 * Listener interface for VideoStreamSelectionPanel.
 * Implementations are notified whenever user chooses a RTSP stream.
 *
 * @author piotr.rozanski@braintech.pl
 */
public interface VideoStreamSelectionListener {

	/**
	 * Called whenevr a user request the camera list to be refreshed.
	 */
	public void refreshRequested();

	/**
	 * Called whenever a user chooses a RTSP stream.
	 *
	 * @param stream  requested stream specification
	 */
	public void videoStreamSelected(VideoStreamSpecification stream);

}
