package org.signalml.app.action.video;

import static org.signalml.app.util.i18n.SvarogI18n._;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.signalml.app.video.VideoFrame;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * Action for opening a window with video camera preview.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class AddCameraAction extends AbstractSignalMLAction {

	private final ViewerElementManager viewerElementManager;

	public AddCameraAction(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		setText(_("Add camera"));
		setIconPath("org/signalml/app/icon/camera.png");
		setToolTip(_("Open preview of the selected camera"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		String url = askUserForVideoStreamURL();
		if (url != null) {
			if (url.startsWith("rtsp://")) {
				VideoFrame videoFrame = new VideoFrame("Camera preview", JFrame.DISPOSE_ON_CLOSE);
				videoFrame.open(url);
				videoFrame.setVisible(true);
				videoFrame.play();
			} else {
				feedbackUserOnWrongURL();
			}
		}
	}

	private String askUserForVideoStreamURL() {
		return JOptionPane.showInputDialog(
			viewerElementManager.getDialogParent(),
			"Enter URL for RTSP video stream:",
			"Add camera preview",
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	private void feedbackUserOnWrongURL() {
		JOptionPane.showMessageDialog(
			viewerElementManager.getDialogParent(),
			"Entered URL is not a valid RTSP video stream.",
			"Could not create camera preview",
			JOptionPane.ERROR_MESSAGE
		);
	}

	/**
	 * Enables this action if and only if
	 * the software support for video preview is available.
	 */
	@Override
	public void setEnabledAsNeeded() {
		setEnabled(VideoFrame.isVideoAvailable());
	}

}
