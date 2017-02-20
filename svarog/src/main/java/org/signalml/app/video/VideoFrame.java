package org.signalml.app.video;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

/**
 * Base class for video display window in Svarog.
 * Implementation is based on the VLCJ project. Therefore, native VLC libraries
 * are necessary for this method to work.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class VideoFrame extends JFrame {

	private static final boolean AVAILABLE = new NativeDiscovery().discover();
	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 400;

	protected final MediaPlayer player;
	protected final EmbeddedMediaPlayerComponent component;
	protected final List<MediaPlayerEventListener> listeners = new LinkedList<>();

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

	/**
	 * Create new Swing frame for displaying video.
	 *
	 * @param mediaPlayerComponent  component for displaying video
	 * @param title  text to be displayed in frame's top bar
	 * @param defaultCloseOperation  one of JFrame.*_ON_CLOSE constants
	 */
	public VideoFrame(EmbeddedMediaPlayerComponent mediaPlayerComponent, String title, int defaultCloseOperation) {
		super(title);
		if (!AVAILABLE) {
			throw new RuntimeException("video playback is unavailable on this machine");
		}
		component = mediaPlayerComponent;
		setDefaultCloseOperation(defaultCloseOperation);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		player = component.getMediaPlayer();
	}

	/**
	 * Add listener object to respond to media player events.
	 *
	 * @param listener  listener object, cannot be null
	 */
	public final void addListener(MediaPlayerEventListener listener) {
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

}
