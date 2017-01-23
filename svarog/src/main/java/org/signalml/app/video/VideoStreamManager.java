package org.signalml.app.video;

import java.util.Map;
import org.signalml.app.worker.monitor.Helper;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.CameraControlRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;

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

	public VideoStreamManager() {
		openbciIpAddress = Helper.getOpenBCIIpAddress();
		openbciPort = Helper.getOpenbciPort();
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
		CameraControlRequest getStreamRequest = new CameraControlRequest("get_stream", stream);
		RequestOKResponse response = (RequestOKResponse) Helper.sendRequestAndParseResponse(
			getStreamRequest,
			openbciIpAddress,
			openbciPort,
			MessageType.REQUEST_OK_RESPONSE
		);
		Map params = (Map) response.getParams();
		currentStreamURL = (String) params.get("url");
		currentStream = stream;
		return currentStreamURL;
	}

	/**
	 * Drop the currently selected stream.
	 * Does nothing if no stream is selected.
	 */
	public synchronized void free() {
		if (currentStreamURL != null) {
			try {
				CameraControlRequest dropStreamRequest = new CameraControlRequest("drop_stream", currentStream);
				Helper.sendRequest(dropStreamRequest, openbciIpAddress, openbciPort, Helper.DEFAULT_RECEIVE_TIMEOUT);
			} catch (OpenbciCommunicationException ex) {
				// stream or OBCI is no longer active
			}
			currentStream = null;
			currentStreamURL = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}

}
