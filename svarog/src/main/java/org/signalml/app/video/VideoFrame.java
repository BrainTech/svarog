package org.signalml.app.video;

import javax.swing.JFrame;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;

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

	/**
	 * Check if video playback is available (VLC libraries are installed).
	 * VideoFrame instances can be created only if this method returns true.
	 *
	 * @return true if video playback is available, false otherwise
	 */
	public static boolean isVideoAvailable() {
		return AVAILABLE;
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
		component = new EmbeddedMediaPlayerComponent();
		setContentPane(component);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		player = component.getMediaPlayer();
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
		player.setTime(time);
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
