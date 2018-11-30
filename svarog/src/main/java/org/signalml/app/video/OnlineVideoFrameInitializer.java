package org.signalml.app.video;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.signalml.app.worker.monitor.GetAvailableVideoWorker;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Asynchronously waits for GetAvailableVideoWorker to finish querying OBCI
 * about available cameras, and then creates (or updates) video frame
 * with a list of currently available cameras.
 */
public class OnlineVideoFrameInitializer implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(OnlineVideoFrameInitializer.class);
	private final GetAvailableVideoWorker worker;
	private final OnlineVideoFrame existingFrame;

	public OnlineVideoFrameInitializer(GetAvailableVideoWorker worker) {
		this(worker, null);
	}

	public OnlineVideoFrameInitializer(GetAvailableVideoWorker worker, OnlineVideoFrame existingFrame) {
		this.worker = worker;
		this.existingFrame = existingFrame;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
			try {
				List<VideoSourceSpecification> cameras = worker.get();
				OnlineVideoFrame videoFrame = existingFrame;
				if (videoFrame == null) {
					videoFrame = new OnlineVideoFrame(_("Camera preview"));
				}
				videoFrame.setAvailableSources(cameras);
				videoFrame.setVisible(true);
			} catch (ExecutionException|InterruptedException e) {
				logger.error("Could not create camera selection dialog", e);
			}
		}
	}

}
