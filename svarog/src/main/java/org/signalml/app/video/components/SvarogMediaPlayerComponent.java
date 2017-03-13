package org.signalml.app.video.components;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * Base media component implementation for use in Svarog.
 * Differs from EmbeddedMediaPlayerComponent only in default VLC settings.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class SvarogMediaPlayerComponent extends EmbeddedMediaPlayerComponent {

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
