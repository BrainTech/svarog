package org.signalml.app.video.components.internal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

/**
 * Low-level media player implementation (VLC-based).
 * Differs from DirectMediaPlayerComponent only in default VLC settings.
 * Instances of this component should NOT be re-used
 * to display another media file, as it may not work as expected.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class DirectMediaPlayerForURL extends DirectMediaPlayerComponent {

	/**
	 * Create a new instance for opening given URL.
	 * @param url  e.g. file://... or rtsp:///...
	 * @param listeners  optional listeners to be added to created player
	 */
	public DirectMediaPlayerForURL(String url, List<MediaPlayerEventListener> listeners) {
		super( (int sourceWidth, int sourceHeight) -> new RV32BufferFormat(sourceWidth, sourceHeight) );
		DirectMediaPlayer player = getMediaPlayer();
		for (MediaPlayerEventListener listener : listeners) {
			player.addMediaPlayerEventListener(listener);
		}
		if (url.startsWith("rtsp://")) {
			player.playMedia(url);
		} else {
			player.startMedia(url);
			player.setPause(true);
		}
	}

	@Override
	protected String[] onGetMediaPlayerFactoryArgs() {
		List<String> videoFlagsList = new LinkedList<>();
		String videoFlagsStr = System.getenv("SVAROG_VIDEO_FLAGS");
		if (videoFlagsStr == null) {
			videoFlagsList.add("--no-overlay");
			videoFlagsList.add("--network-caching");
			videoFlagsList.add("500");
			videoFlagsList.add("--rtsp-frame-buffer-size");
			videoFlagsList.add("40000");
		} else {
			videoFlagsList.addAll(Arrays.asList(videoFlagsStr.split(" ")));
		}
		return videoFlagsList.toArray(new String[videoFlagsList.size()]);
	}

}
