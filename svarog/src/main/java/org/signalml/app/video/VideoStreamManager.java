package org.signalml.app.video;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.signalml.app.worker.monitor.Helper;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.CameraControlRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Communicates with OBCI in order to initialize and drop streams.
 * Instances of this class can be asked to replace currently selected stream
 * with a RTSP stream created for given specification,
 * or to free the stream previously selected.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class VideoStreamManager {

	private final String openbciIpAddress;
	private final int openbciPort;

	private VideoStreamSpecification currentStream;
	private String currentStreamURL;

	private static final Set<VideoStreamManager> INSTANCES = new HashSet<>();

	public VideoStreamManager() {
		openbciIpAddress = Helper.getOpenBCIIpAddress();
		openbciPort = Helper.getOpenbciPort();
		// store this instance so it will be disposed when Svarog exits
		synchronized (INSTANCES) {
			INSTANCES.add(this);
		}
	}

	/**
	 * @return specification of the currently selected stream
	 * or null if no stream has been selected
	 */
	public synchronized VideoStreamSpecification getCurrentStream() {
		return currentStream;
	}

	/**
	 * @return currently selected RTSP URL of the stream
	 * or null if no stream has been selected
	 */
	public synchronized String getCurrentStreamURL() {
		return currentStreamURL;
	}

	/**
	 * Request a RTSP stream with given specification.
	 * If any other stream has been previously requested, the previous
	 * stream is stopped before the new one will be requested.
	 *
	 * @param stream  stream specification
	 * @return  RTSP URL of the requested stream
	 * @throws OpenbciCommunicationException  if OBCI cannot provide the stream
	 */
	public synchronized String replace(VideoStreamSpecification stream) throws OpenbciCommunicationException {
		free();
		RequestOKResponse response = sendRequest(new CameraControlRequest("get_stream", stream));
		Map params = (Map) response.getParams();
		currentStreamURL = (String) params.get("url");
		currentStream = stream;
		return currentStreamURL;
	}

	/**
	 * Send a custom request to camera server.
	 *
	 * @param request  request object
	 * @return  response object
	 * @throws OpenbciCommunicationException  if action did not succeed
	 */
	public synchronized RequestOKResponse sendRequest(CameraControlRequest request) throws OpenbciCommunicationException {
		return (RequestOKResponse) Helper.sendRequestAndParseResponse(
			request,
			openbciIpAddress,
			openbciPort,
			MessageType.REQUEST_OK_RESPONSE
		);
	}

	/**
	 * Send a custom request to camera server.
	 * Required field "cam_id" will be added automatically.
	 *
	 * @param request  request object
	 * @return  response object
	 * @throws OpenbciCommunicationException  if action did not succeed
	 */
	public synchronized RequestOKResponse sendRequestWithCameraID(CameraControlRequest request) throws OpenbciCommunicationException {
		if (currentStream == null) {
			throw new OpenbciCommunicationException(_("no current stream"));
		}
		request.putArg("cam_id", currentStream.cameraID);
		return sendRequest(request);
	}

	/**
	 * Drop the currently selected stream.
	 * Does nothing if no stream is selected.
	 */
	public synchronized void free() {
		if (currentStreamURL != null) {
			try {
				CameraControlRequest dropStreamRequest = new CameraControlRequest("drop_stream", currentStream);
				Helper.sendRequestAndParseResponse(dropStreamRequest, openbciIpAddress, openbciPort, MessageType.REQUEST_OK_RESPONSE);
			} catch (OpenbciCommunicationException ex) {
				// stream or OBCI is no longer active
			}
			currentStream = null;
			currentStreamURL = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		synchronized (INSTANCES) {
			INSTANCES.remove(this);
		}
		free();
		super.finalize();
	}

	/**
	 * Release all RTSP streams.
	 * This includes notifying OBCI that video streams are no longer needed.
	 */
	public static void freeAllStreams() {
		List<VideoStreamManager> instancesToFree;
		synchronized (INSTANCES) {
			instancesToFree = new ArrayList<>(INSTANCES);
			INSTANCES.clear();
		}
		for (VideoStreamManager manager : instancesToFree) {
			manager.free();
		}
	}

	/**
	 * @return list of specifications of all currently active streams
	 */
	public static List<VideoStreamSpecification> getAllActiveStreams() {
		List<VideoStreamSpecification> streams = new LinkedList<>();
		synchronized (INSTANCES) {
			for (VideoStreamManager instance: INSTANCES) {
				VideoStreamSpecification stream = instance.getCurrentStream();
				if (stream != null) {
					streams.add(stream);
				}
			}
		}
		return streams;
	}

}
