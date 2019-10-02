package org.signalml.app.video;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import org.signalml.app.video.components.SvarogMediaComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Base class for video display window in Svarog.
 * Implementation is based on the VLCJ project. Therefore, native VLC libraries
 * are necessary for this method to work.
 *
 * @author piotr.rozanski@braintech.pl
 * @param <T>  actual media component subclass
 */
public class VideoFrame<T extends SvarogMediaComponent> extends JFrame {

	private static final boolean AVAILABLE = new NativeDiscovery().discover();
	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 400;

	public final T component;
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
	public VideoFrame(T mediaPlayerComponent, String title, int defaultCloseOperation) {
		super(title);
		if (!AVAILABLE) {
			throw new RuntimeException(_("video playback is unavailable on this machine"));
		}
		component = mediaPlayerComponent;
		setDefaultCloseOperation(defaultCloseOperation);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * Add listener object to respond to media player events.
	 *
	 * @param listener  listener object, cannot be null
	 */
	public final void addListener(MediaPlayerEventListener listener) {
		component.addMediaPlayerEventListener(listener);
		listeners.add(listener);
	}

	/**
	 * Close the window and release all window's and player's resources.
	 */
	@Override
	public void dispose() {
		component.release();
		super.dispose();
	}

}
