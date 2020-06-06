package org.signalml.app.action.video;

import java.awt.event.ActionEvent;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.video.OnlineVideoFrameInitializer;
import org.signalml.app.video.VideoFrame;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.monitor.GetAvailableVideoWorker;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * Action for opening a window with video camera preview.
 * This action starts a GetAvailableVideoWorker instance,
 * and only after its asynchronous completion, creates a video frame.
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
		if (!VideoFrame.isVideoAvailable()) {
			return;
		}
		GetAvailableVideoWorker worker = new GetAvailableVideoWorker(viewerElementManager.getDialogParent());
		worker.addPropertyChangeListener(new OnlineVideoFrameInitializer(worker));
		worker.execute();
	}

}
