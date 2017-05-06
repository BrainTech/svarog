package org.signalml.app.video.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.signalml.app.video.VideoStreamManager;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.CameraControlRequest;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import static org.signalml.app.util.i18n.SvarogI18n._;


/**
 * Media component with additional reconnecting support.
 * Whenever RTSP communication fails, player tries to reconnect with the same URL.
 * Every component instance owns an internal instance of VideoStreamManager.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class OnlineMediaComponent extends SvarogMediaComponent {

	private static final Logger logger = Logger.getLogger(OnlineMediaComponent.class);

	private static final int WAIT_BEFORE_RECONNECT_MILLIS = 100;

	private final VideoStreamManager manager;
	
	private static int MAX_RECONNECTS = 5;
	
	private int reconnectCount;

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
			reconnectCount += 1;
			if (reconnectCount == MAX_RECONNECTS)
			{
				reportReconnects();
			}
			
			tryAgain.setRepeats(false);
			tryAgain.start();
		}
	}

	public OnlineMediaComponent() {
		reconnectCount = 0;
		this.manager = new VideoStreamManager();
		addMediaPlayerEventListener(new MediaPlayerErrorListener());
	}

	public VideoStreamManager getManager() {
		return manager;
	}

	public void reportReconnects() {
		String rtspURL = manager.getCurrentStreamURL();
		Dialogs.showWarningMessage(_("Failed due to error in VLC preview")+"\n"
			+ _("Video preview will not work until you restart Svarog.")+"\n"
			+ _("Video saving will continue to operate normally.")+"\n"
			+ _("You can open preview in any other video player, by opening this link:")+"\n"+rtspURL);
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
