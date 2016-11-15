package org.signalml.app.video;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

/**
 * Video display window in Svarog.
 * Implementation is based on the VLCJ project. Therefore, native VLC libraries
 * are necessary for this method to work.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class VideoFrame extends JFrame {

	private static final boolean AVAILABLE = new NativeDiscovery().discover();
	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 400;

	private final MediaPlayer player;
	private final EmbeddedMediaPlayerComponent component;
	private final List<MediaPlayerEventListener> listeners = new LinkedList<MediaPlayerEventListener>();

	private volatile Long duration;

	/**
	 * Check if video playback is available (VLC libraries are installed).
	 * VideoFrame instances can be created only if this method returns true.
	 * Internal VLC instance will NOT automatically read settings in ~/.config/vlc.
	 *
	 * @return true if video playback is available, false otherwise
	 */
	public static boolean isVideoAvailable() {
		return AVAILABLE;
	}

	private static String[] getVideoFlags() {
		List<String> videoFlagsList = new LinkedList<String>();
		String videoFlagsStr = System.getenv("SVAROG_VIDEO_FLAGS");
		if (videoFlagsStr == null) {
			videoFlagsList.add("--no-overlay");
		} else {
			videoFlagsList.addAll(Arrays.asList(videoFlagsStr.split(" ")));
		}
		return videoFlagsList.toArray(new String[videoFlagsList.size()]);
	}

	/**
	 * Create new Swing frame for displaying video.
	 *
	 * @param title  text to be displayed in frame's top bar
	 */
	public VideoFrame(String title) {
		super(title);
		if (!AVAILABLE) {
			throw new RuntimeException("video playback is unavailable on this machine");
		}
		component = new EmbeddedMediaPlayerComponent() {
			@Override
			protected String[] onGetMediaPlayerFactoryArgs() {
				return getVideoFlags();
			}
		};
		setContentPane(component);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		player = component.getMediaPlayer();
		player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
				// once the duration is known, we store it inside this class
				duration = newDuration;
			}
		});
	}

	/**
	 * Add listener object to respond to media player events.
	 *
	 * @param listener  listener object, cannot be null
	 */
	public void addListener(MediaPlayerEventListener listener) {
		player.addMediaPlayerEventListener(listener);
		listeners.add(listener);
	}

	/**
	 * Close the window and release all window's and player's resources.
	 */
	@Override
	public void dispose() {
		player.release();
		super.dispose();
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
	 * Prepare a new media item for playback, but do not begin playing.
	 *
	 * When playing files, depending on the run-time Operating System it may be necessary to pass a URL here
	 * (beginning with "file://") rather than a local file path.
	 *
	 * @param path  path to the video file
	 */
	public void open(String path) {
		player.prepareMedia(path);
	}

	/**
	 * Begin playback.
	 *
	 * If called when the play-back is paused, the play-back will resume from the current position.
	 */
	public void play() {
		player.play();
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

}
