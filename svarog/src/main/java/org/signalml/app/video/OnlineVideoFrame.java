package org.signalml.app.video;

import org.signalml.app.video.components.VideoStreamSelectionPanel;
import org.signalml.app.video.components.ImageSeparator;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.signalml.app.video.components.VideoStreamSelectionListener;
import org.signalml.app.worker.monitor.GetAvailableVideoWorker;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Video frame for displaying on-line RTSP streams.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class OnlineVideoFrame extends VideoFrame {

	private static final int WAIT_BEFORE_RECONNECT_MILLIS = 100;

	private final VideoStreamManager manager;
	private final VideoStreamSelectionPanel streamSelectionPanel;
	private final JPanel sidePanel;

	private static final Set<OnlineVideoFrame> instances = new HashSet<>();

	/**
	 * Reacts on user selecting one of the available streams,
	 * or requesting refresh of the camera list.
	 */
	private class UserSelectionListener implements VideoStreamSelectionListener {

		@Override
		public void refreshRequested() {
			GetAvailableVideoWorker worker = new GetAvailableVideoWorker(getParent());
			worker.addPropertyChangeListener(new OnlineVideoFrameInitializer(worker, OnlineVideoFrame.this));
			worker.execute();
		}

		@Override
		public void videoStreamSelected(VideoStreamSpecification stream) {
			if (!stream.equals(manager.getCurrentStream())) {
				player.stop();
				try {
					String rtspURL = manager.replace(stream);
					open(rtspURL);
					play();
				} catch (OpenbciCommunicationException ex) {
					streamSelectionPanel.clearSelection();
					ex.showErrorDialog("Error initializing video preview");
				}
			}
		}

	}

	/**
	 * Forces reconnect on VLC player errors.
	 */
	private class MediaPlayerErrorListener extends MediaPlayerEventAdapter {
		@Override
		public void error(MediaPlayer player) {
			final Timer tryAgain = new Timer(WAIT_BEFORE_RECONNECT_MILLIS, (ActionEvent e) -> {
				String rtspURL = manager.getCurrentStreamURL();
				if (rtspURL != null) {
					open(rtspURL);
					play();
				}
			});
			tryAgain.setRepeats(false);
			tryAgain.start();
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
		super(title, JFrame.DISPOSE_ON_CLOSE);

		// communicates with OBCI when needed
		manager = new VideoStreamManager();

		// panel allowing user to select video source (camera) and stream
		streamSelectionPanel = new VideoStreamSelectionPanel(new UserSelectionListener());

		// listen to error messages from media player and reconnect
		addListener(new MediaPlayerErrorListener());

		// place this panel at the right hand side, with a possibility
		// to hide it or show it by clicking on the separator
		sidePanel = new JPanel(new BorderLayout());
		ImageSeparator separator = new ImageSeparator("org/signalml/app/icon/clicktotoggle.png");
		separator.addMouseListener(new SidePanelToggleListener());
		sidePanel.add(separator, BorderLayout.WEST);
		sidePanel.add(streamSelectionPanel, BorderLayout.CENTER);

		// place video playback component in the center of the frame
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(component, BorderLayout.CENTER);
		mainPanel.add(sidePanel, BorderLayout.EAST);
		setContentPane(mainPanel);

		// store this instance so it will be disposed when Svarog exits
		instances.add(this);
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
		manager.free();
		instances.remove(this);
	}

	/**
	 * Close all opened windows and release windows' and players' resources.
	 * This includes notifying OBCI that video streams are no longer needed.
	 */
	public static void disposeAll() {
		List<OnlineVideoFrame> instancesToDispose = new ArrayList<>(instances);
		instances.clear();
		for (OnlineVideoFrame frame : instancesToDispose) {
			frame.dispose();
		}
	}

}
