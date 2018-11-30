package org.signalml.app.action.video;

import java.awt.event.ActionEvent;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.signalml.app.video.OfflineVideoFrame;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Action for video play/pause button.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class PlayPauseVideoAction extends AbstractSignalMLAction {

	private final OfflineVideoFrame videoFrame;
	private final JSlider videoRateSlider;
	private final Runnable onClickWhenPaused = new Runnable() {
		@Override
		public void run() {
			videoFrame.play();
		}
	};
	private final Runnable onClickWhenPlaying = new Runnable() {
		@Override
		public void run() {
			videoFrame.pause();
		}
	};
	private Runnable onClick; // accessed from Swing thread

	/**
	 * Internal listener for "video rate" slider.
	 */
	private class VideoRateSliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			int percentage = videoRateSlider.getValue();
			videoFrame.setRate(0.01f * percentage);
		}
	}

	/**
	 * Internal listener, connected to the VideoFrame.
	 */
	private class VideoFrameListener extends MediaPlayerEventAdapter {

		private void updateActionState(MediaPlayer mp) {
			final boolean isPlaying = mp.isPlaying();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					onClick = isPlaying ? onClickWhenPlaying : onClickWhenPaused;
					setIconPath(composeIconPath(isPlaying ? "suspend" : "resume"));
				}
			});
		}

		@Override
		public void paused(MediaPlayer mp) {
			updateActionState(mp);
		}

		@Override
		public void playing(MediaPlayer mp) {
			setEnabled(true);
			updateActionState(mp);
		}

		@Override
		public void finished(MediaPlayer mp) {
			setEnabled(false);
			updateActionState(mp);
		}
	}

	/**
	 * Create a new action instance for play/pause button.
	 *
	 * @param videoFrame  created video frame, must not be null
	 */
	public PlayPauseVideoAction(OfflineVideoFrame videoFrame) {
		this.videoFrame = videoFrame;
		this.videoRateSlider = new VideoRateSlider();
		this.onClick = onClickWhenPaused;
		setText(_("Play/pause video preview"));
		setIconPath(composeIconPath("resume"));
		videoFrame.addListener(new VideoFrameListener());
		videoRateSlider.addChangeListener(new VideoRateSliderListener());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		onClick.run();
	}

	private static String composeIconPath(String iconName) {
		return "org/signalml/app/icon/"+iconName+".png";
	}

	public JSlider getVideoRateSlider() {
		return videoRateSlider;
	}

}
