package org.signalml.app.video;

import org.signalml.app.video.components.OnlineMediaComponent;
import org.signalml.app.video.components.VideoStreamSelectionPanel;
import org.signalml.app.video.components.ImageSeparator;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.signalml.app.video.components.OnlineMediaPlayerPanel;
import org.signalml.app.video.components.VideoStreamSelectionListener;
import org.signalml.app.worker.monitor.GetAvailableVideoWorker;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Video frame for displaying on-line RTSP streams and a list
 * of available streams.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class OnlineVideoFrame extends VideoFrame<OnlineMediaComponent> {

	private final VideoStreamManager manager;
	private final VideoStreamSelectionPanel streamSelectionPanel;
	private final JPanel sidePanel;
	private final OnlineMediaPlayerPanel previewPanel;

	/**
	 * Reacts on user selecting one of the available streams,
	 * or requesting refresh of the camera list.
	 */
	private class UserSelectionListener implements VideoStreamSelectionListener {

		@Override
		public void previewRequested(VideoStreamSpecification stream) {
			// not possible in this scenario
		}

		@Override
		public void refreshRequested() {
			GetAvailableVideoWorker worker = new GetAvailableVideoWorker(getParent());
			worker.addPropertyChangeListener(new OnlineVideoFrameInitializer(worker, OnlineVideoFrame.this));
                        worker.execute();
		}

		@Override
		public void videoStreamSelected(VideoStreamSpecification stream) {
                    VideoStreamSelectedWorker change_stream_worker = new VideoStreamSelectedWorker(previewPanel, manager, component, streamSelectionPanel, stream);
                    if (!stream.equals(manager.getCurrentStream())) {

                        change_stream_worker.executeWithWialog();


                    }
		}

	}

	/**
	 * Reacts on user toggling visibility of the side panel.
	 */
	private class SidePanelToggleListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (streamSelectionPanel.getParent() == sidePanel) {
				sidePanel.remove(streamSelectionPanel);
			} else {
				sidePanel.add(streamSelectionPanel, BorderLayout.CENTER);
			}
			pack();
		}
	}

	/**
	 * Create a new video frame for displaying RTSP stream from OBCI.
	 *
	 * @param title  human-readable description to be displayed in the top bar
	 */
	public OnlineVideoFrame(String title) {
		super(new OnlineMediaComponent(), title, JFrame.DISPOSE_ON_CLOSE);

		// communicates with OBCI when needed
		manager = component.getManager();

		// panel allowing user to select video source (camera) and stream
		streamSelectionPanel = new VideoStreamSelectionPanel(new UserSelectionListener());

		// place this panel at the right hand side, with a possibility
		// to hide it or show it by clicking on the separator
		sidePanel = new JPanel(new BorderLayout());
		ImageSeparator separator = new ImageSeparator("org/signalml/app/icon/clicktotoggle.png");
		separator.addMouseListener(new SidePanelToggleListener());
		sidePanel.add(separator, BorderLayout.WEST);
		sidePanel.add(streamSelectionPanel, BorderLayout.CENTER);

		// create panel with video preview and PTZ buttons
		previewPanel = new OnlineMediaPlayerPanel(component);

		// place video playback component in the center of the frame
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(previewPanel, BorderLayout.CENTER);
		mainPanel.add(sidePanel, BorderLayout.EAST);
		setContentPane(mainPanel);
	}

	/**
	 * Update displayed list of available sources.
	 * Given list of specifications should not be modified
	 * after being passed to this method.
	 *
	 * @param cameras  list of video source capabilities
	 */
	public void setAvailableSources(List<VideoSourceSpecification> cameras) {
		streamSelectionPanel.setAvailableSources(cameras);
	}

	/**
	 * Close the window and release all window's and player's resources.
	 * This includes notifying OBCI that the video stream is no longer needed.
	 */
	@Override
	public void dispose() {
		super.dispose();
		component.release();
		manager.free();
	}

}
