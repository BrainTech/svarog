package org.signalml.app.video.components;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

/**
 * Media component with additional methods for managing playback
 * of (off-line) video files.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class OfflineMediaComponent extends SvarogMediaComponent {

	// media duration in milliseconds
	private volatile Long duration;

	public OfflineMediaComponent() {
		addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
				// once the duration is known, we store it inside this class
				duration = newDuration;
			}
		});
	}

	/**
	 * Check whether the video position (time) can be changed while playing.
	 *
	 * @return TRUE if video position can be changed, FALSE otherwise
	 */
	public boolean isSeekable() {
		return direct != null && duration != null && direct.getMediaPlayer().isSeekable();
	}

	/**
	 * Pause play-back.
	 *
	 * If the play-back is currently paused it will begin playing.
	 */
	public void pause() {
		if (direct != null) {
			direct.getMediaPlayer().pause();
		}
	}

	/**
	 * Begin playback.
	 *
	 * If called when the play-back is paused, the play-back will resume from the current position.
	 */
	public void play() {
		if (direct != null) {
			direct.getMediaPlayer().play();
		}
	}

	/**
	 * Set the video play rate.
	 *
	 * Some media protocols are not able to change the rate.
	 *
	 * @param rate  rate, where 1.0 is normal speed, 0.5 is half speed, 2.0 is double speed and so on
	 */
	public void setRate(float rate) {
		if (direct != null) {
			direct.getMediaPlayer().setRate(rate);
		}
	}

	/**
	 * Jump to a specific moment.
	 *
	 * @param time  time since the beginning, in milliseconds
	 */
	public void setTime(long time) {
		if (direct == null || time < 0 || (duration != null && time >= duration)) {
			// if media is not loaded or given time is outside signal extent
			return;
		}
		MediaPlayer player = direct.getMediaPlayer();
		boolean isNotPlaying = !player.isPlaying();
		if (isNotPlaying) {
			// fix to make sure player is not in "stopped" state
			// because it is not possible to change position in this state
			player.start();
			player.setPause(true);
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
