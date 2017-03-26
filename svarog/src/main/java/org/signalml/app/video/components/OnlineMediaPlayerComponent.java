package org.signalml.app.video.components;

import java.awt.event.ActionEvent;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.signalml.app.video.VideoStreamManager;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.CameraControlRequest;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Media player component with additional reconnecting support.
 * Whenever RTSP communication fails, player tries to reconnect with the same URL.
 * Every component instance owns an internal instance of VideoStreamManager.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class OnlineMediaPlayerComponent extends SvarogMediaPlayerComponent {

	private static final Logger logger = Logger.getLogger(OnlineMediaPlayerComponent.class);

	private static final int WAIT_BEFORE_RECONNECT_MILLIS = 100;

	private final VideoStreamManager manager;

	/**
	 * Forces reconnect on VLC player errors.
	 */
	private class MediaPlayerErrorListener extends MediaPlayerEventAdapter {
		@Override
		public void error(MediaPlayer player) {
			final Timer tryAgain = new Timer(WAIT_BEFORE_RECONNECT_MILLIS, (ActionEvent e) -> {
				String rtspURL = manager.getCurrentStreamURL();
				if (rtspURL != null) {
					player.prepareMedia(rtspURL);
					player.play();
				}
			});
			tryAgain.setRepeats(false);
			tryAgain.start();
		}
	}

	public OnlineMediaPlayerComponent() {
		this.manager = new VideoStreamManager();
		getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerErrorListener());
	}

	public VideoStreamManager getManager() {
		return manager;
	}

	/**
	 * Send a custom request to camera server.
	 *
	 * @param request  request object
	 */
	public void sendCameraRequest(CameraControlRequest request) {
		try {
			manager.sendRequestWithCameraID(request);
		} catch (OpenbciCommunicationException ex) {
			logger.error("camera request failed", ex);
		}
	}

}
