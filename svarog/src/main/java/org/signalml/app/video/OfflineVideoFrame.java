package org.signalml.app.video;

import org.signalml.app.video.components.SvarogMediaPlayerComponent;
import javax.swing.JFrame;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

/**
 * Video frame for displaying off-line video files.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class OfflineVideoFrame extends VideoFrame {

	// media duration in milliseconds
	private volatile Long duration;

	public OfflineVideoFrame(String title) {
		super(new SvarogMediaPlayerComponent(), title, JFrame.DO_NOTHING_ON_CLOSE);
		setContentPane(component);
		player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
				// once the duration is known, we store it inside this class
				duration = newDuration;
			}
		});
	}

	/**
	 * Start the video and pause it immediately.
	 *
	 * It will ensure that playback is not in the "stopped" state.
	 */
	public void init() {
		player.start();
		player.setPause(true);
	}

	/**
	 * Check whether the video position (time) can be changed while playing.
	 *
	 * @return TRUE if video position can be changed, FALSE otherwise
	 */
	public boolean isSeekable() {
		return player.isSeekable() && duration != null;
	}

	/**
	 * Pause play-back.
	 *
	 * If the play-back is currently paused it will begin playing.
	 */
	public void pause() {
		player.pause();
	}

	/**
	 * Set the video play rate.
	 *
	 * Some media protocols are not able to change the rate.
	 *
	 * @param rate  rate, where 1.0 is normal speed, 0.5 is half speed, 2.0 is double speed and so on
	 */
	public void setRate(float rate) {
		player.setRate(rate);
	}

	/**
	 * Jump to a specific moment.
	 *
	 * @param time  time since the beginning, in milliseconds
	 */
	public void setTime(long time) {
		if (time < 0 || (duration != null && time >= duration)) {
			// if given time is outside signal extent
			return;
		}
		boolean isNotPlaying = !player.isPlaying();
		if (isNotPlaying) {
			// fix to make sure player is not in "stopped" state
			// because it is not possible to change position in this state
			init();
		}
		if (duration != null && time < duration) {
			// duration should be known after returning from start(),
			// otherwise the video position cannot be changed
			player.setTime(time);
			if (isNotPlaying) {
				for (MediaPlayerEventListener listener : listeners) {
					// this is needed, because MediaPlayer does not send
					// timeChanged events when paused
					listener.timeChanged(player, time);
				}
			}
		}
	}

}
