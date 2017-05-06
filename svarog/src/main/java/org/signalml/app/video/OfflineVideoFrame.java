package org.signalml.app.video;

import javax.swing.JFrame;
import org.signalml.app.video.components.OfflineMediaComponent;

/**
 * Video frame for displaying off-line video files.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class OfflineVideoFrame extends VideoFrame<OfflineMediaComponent> {

	public OfflineVideoFrame(String title) {
		super(new OfflineMediaComponent(), title, JFrame.DO_NOTHING_ON_CLOSE);
		setContentPane(component);
	}

}
