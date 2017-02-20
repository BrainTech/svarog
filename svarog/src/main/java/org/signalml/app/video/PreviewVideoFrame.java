package org.signalml.app.video;

import org.signalml.app.video.components.OnlineMediaPlayerComponent;
import javax.swing.JFrame;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;

/**
 * Simple video frame for displaying only a single RTSP stream.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class PreviewVideoFrame extends VideoFrame {

	private final VideoStreamManager manager;
	private final String rtspURL;

	/**
	 * Create a new video frame for displaying RTSP stream from OBCI.
	 *
	 * @param stream  specification of the stream to be displayed
	 * @throws OpenbciCommunicationException if RTSP URL cannot be acquired
	 */
	public PreviewVideoFrame(VideoStreamSpecification stream) throws OpenbciCommunicationException {
		super(new OnlineMediaPlayerComponent(), "video preview", JFrame.DISPOSE_ON_CLOSE);
		manager = ((OnlineMediaPlayerComponent) component).getManager();
		rtspURL = manager.replace(stream);
		setContentPane(component);
	}

	@Override
	public void setVisible(boolean b) {
		if (!b) {
			// stop player when hiding window
			player.stop();
		}
		super.setVisible(b);
		if (b) {
			// start player when window is shown
			player.playMedia(rtspURL);
		}
	}

	/**
	 * Close the window and release all window's and player's resources.
	 * This includes notifying OBCI that the video stream is no longer needed.
	 */
	@Override
	public void dispose() {
		player.stop();
		manager.free();
		super.dispose();
	}

}
