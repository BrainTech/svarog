package org.signalml.app.worker.monitor;

import java.awt.Container;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.video.VideoSourceSpecification;
import org.signalml.app.video.VideoStreamSpecification;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.CameraControlRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;

/**
 * Queries the OBCI server for available video sources and stream presets.
 * Gathered list of available cameras is returned as this worker's result.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class GetAvailableVideoWorker extends SwingWorkerWithBusyDialog<List<VideoSourceSpecification>, VideoSourceSpecification> {

	protected static final Logger logger = Logger.getLogger(GetAvailableVideoWorker.class);

	private final String openbciIpAddress;
	private final int openbciPort;

	private final List<VideoSourceSpecification> results;

	/**
	 * Prepare a worker.
	 *
	 * @param parentContainer  container for the Swing "busy" dialog to be displayed
	 */
	public GetAvailableVideoWorker(Container parentContainer) {
		super(parentContainer);
		openbciIpAddress = Helper.getOpenBCIIpAddress();
		openbciPort = Helper.getOpenbciPort();
		results = new LinkedList<>();
	}

	@Override
	protected List<VideoSourceSpecification> doInBackground() throws OpenbciCommunicationException {
		publishAvailableCameras();
		return results;
	}

	@Override
	protected void process(List<VideoSourceSpecification> chunks) {
		results.addAll(chunks);
	}

	private void publishAvailableCameras() throws OpenbciCommunicationException {
		CameraControlRequest findCamerasRequest = new CameraControlRequest("find_cameras");
		RequestOKResponse response = (RequestOKResponse) Helper.sendRequestAndParseResponse(findCamerasRequest, openbciIpAddress, openbciPort, MessageType.REQUEST_OK_RESPONSE);
		response.getParams().forEach((String cameraID, Object dictionary) -> {
			try {
				Map params = (Map) dictionary;
				String description = (String) params.get("model");
				int cameraFeatures = parseCameraFeatures(params);
				Map presets = (Map) params.get("available_presets");
				VideoSourceSpecification camera = new VideoSourceSpecification(
					description,
					parseReturnedStreams(description, cameraID, cameraFeatures, presets)
				);
				publish(camera);
			} catch (Throwable t) {
				logger.error("received invalid description for camera "+cameraID, t);
			}
		});
	}

	private static int parseCameraFeatures(Map params) {
		int cameraFeatures = 0;
		if (Boolean.TRUE.equals(params.get("has_relative_pan"))) {
			cameraFeatures |= VideoStreamSpecification.FEATURE_PAN;
		}
		if (Boolean.TRUE.equals(params.get("has_relative_tilt"))) {
			cameraFeatures |= VideoStreamSpecification.FEATURE_TILT;
		}
		if (Boolean.TRUE.equals(params.get("has_relative_zoom"))) {
			cameraFeatures |= VideoStreamSpecification.FEATURE_ZOOM;
		}
		if (Boolean.TRUE.equals(params.get("has_home_position"))) {
			cameraFeatures |= VideoStreamSpecification.FEATURE_HOME;
		}
		if (Boolean.TRUE.equals(params.get("has_night_mode"))) {
			cameraFeatures |= VideoStreamSpecification.FEATURE_NIGHT_MODE;
		}
		return cameraFeatures;
	}

	private static List<VideoStreamSpecification> parseReturnedStreams(String cameraName, String cameraID, int cameraFeatures, Map<String, Object> streamData) {
		List<VideoStreamSpecification> streams = new LinkedList<>();
		streamData.forEach((String streamID, Object dictionary) -> {
			try {
				Map params = (Map) dictionary;
				streams.add(parseReturnedStream(cameraName, cameraID, cameraFeatures, streamID, params));
			} catch (Throwable t) {
				logger.error("received invalid description for stream "+streamID, t);
			}
		});
		return streams;
	}

	protected static VideoStreamSpecification parseReturnedStream(String cameraName, String cameraID, int cameraFeatures, String streamID, Map params) {
		int width = (Integer) params.get("width");
		int height = (Integer) params.get("height");
		double fps = (Double) params.get("fps");
		return new VideoStreamSpecification(cameraName, cameraID, streamID, width, height, (float) fps, cameraFeatures);
	}

	@Override
	protected void done() {
		if (isCancelled()) {
			Helper.cancelReceiving();
		}
		super.done();

		try {
			get();
		} catch (CancellationException e) {
			// user cancelled the execution of this worker
		} catch (InterruptedException|ExecutionException e) {
			if (e.getCause() instanceof OpenbciCommunicationException) {
				OpenbciCommunicationException exception = (OpenbciCommunicationException) e.getCause();
				exception.showErrorDialog(_("An error occurred in communication with video server"));
			} else {
				logger.error("Error in communication with video server", e);
			}
		}
	}

}
