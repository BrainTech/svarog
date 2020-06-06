package org.signalml.app.video;

import javax.swing.JFrame;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.video.components.OnlineMediaComponent;
import org.signalml.app.video.components.OnlineMediaPlayerPanel;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;

/**
 * Simple video frame for displaying only a single RTSP stream.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class PreviewVideoFrame extends VideoFrame<OnlineMediaComponent> {

	private final VideoStreamManager manager;
	private final String rtspURL;

	/**
	 * Create a new video frame for displaying RTSP stream from OBCI.
	 *
	 * @param stream  specification of the stream to be displayed
	 * @throws OpenbciCommunicationException if RTSP URL cannot be acquired
	 */
	public PreviewVideoFrame(VideoStreamSpecification stream) throws OpenbciCommunicationException {
		super(new OnlineMediaComponent(), _("video preview"), JFrame.DISPOSE_ON_CLOSE);
		manager = component.getManager();
		rtspURL = manager.replace(stream);
		OnlineMediaPlayerPanel previewPanel = new OnlineMediaPlayerPanel(component);
		previewPanel.setCameraFeatures(stream.features);
		setContentPane(previewPanel);
	}

	@Override
	public void setVisible(boolean b) {
		if (!b) {
			// stop player when hiding window
			component.release();
		}
		super.setVisible(b);
		if (b) {
			// start player when window is shown
			component.open(rtspURL);
		}
	}

	/**
	 * Close the window and release all window's and player's resources.
	 * This includes notifying OBCI that the video stream is no longer needed.
	 */
	@Override
	public void dispose() {
		component.release();
		manager.free();
		super.dispose();
	}

}
