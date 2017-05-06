package org.signalml.app.video.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import org.signalml.app.video.components.internal.DirectMediaPlayerForURL;
import org.signalml.app.video.components.internal.SvarogRenderCallback;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.RenderCallback;

/**
 * Base visual component for displaying video in Svarog.
 * Internally it uses DirectMediaPlayerForURL, which renders the video content
 * to SvarogRenderCallback's internal BufferedImage instance, which, in turn,
 * renders it onto this component.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class SvarogMediaComponent extends JComponent {

	private final SvarogRenderCallback render;
	protected final List<MediaPlayerEventListener> listeners;
	protected DirectMediaPlayerForURL direct;

	/**
	 * Create a new component.
	 * It will be empty until the first call to open().
	 */
	public SvarogMediaComponent() {
		render = new SvarogRenderCallback(this);
		listeners = new LinkedList<>();
	}

    /**
     * Add a listener to be notified of media player events.
     *
     * @param listener object to notify
     */
	public final void addMediaPlayerEventListener(MediaPlayerEventListener listener) {
		listeners.add(listener);
		if (direct != null) {
			direct.getMediaPlayer().addMediaPlayerEventListener(listener);
		}
	}

	/**
	 * Open a given URL. If it is a off-line (file:///...)  URL, it will be
	 * opened, but not started. If it is a remote (rtsp:///...) URL,
	 * it will be started immediately.
	 *
	 * @param url URL of a media file.
	 */
	public void open(String url) {
		release();
		direct = new DirectMediaPlayerForURL(url, listeners) {
			@Override
			protected RenderCallback onGetRenderCallback() {
				return render;
			}
		};
		render.setAllowedMediaPlayer(direct.getMediaPlayer());
	}

	/**
	 * Close the currently opened media, if any.
	 */
	public void release() {
		render.setAllowedMediaPlayer(null);
		if (direct != null) {
			direct.release(true);
			direct = null;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		render.render((Graphics2D) g, getWidth(), getHeight());
	}

}
