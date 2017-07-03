package org.signalml.app.worker.monitor;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.AmplifierType;
import org.signalml.app.worker.monitor.messages.LauncherMessage;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.zeromq.ZMQ; 

/**
 * This class allows to receive longer OBCI requests.
 * For example - searchigng for active EEG experiments in whole network.
 * We are asking OBCI using Rep-Req, and after we receive "OK" we wait for 
 * data on pull socket.
 *
 * @author Marian Dovgialo
 */
public class ObciPullSocket{
	private final ZMQ.Context zmqContext;
	private ZMQ.Socket pullsocket;
	private int pullSocketPort;
	private static int MIN_PORT_RANGE = 50000;
	private static int port_range_min = MIN_PORT_RANGE;
	
	private int RECEIVE_TIMEOUT_MS = 100;
	

	public ObciPullSocket(){
		zmqContext = ZMQ.context(1);
		pullsocket = zmqContext.socket(ZMQ.PULL);
		pullSocketPort = pullsocket.bindToRandomPort("tcp://*", getMinPort(), 65535);
		pullsocket.setLinger(0);
		pullsocket.setReceiveTimeOut(RECEIVE_TIMEOUT_MS);
	}
        
        
        public ObciPullSocket(int timeout){
                this();                
		pullsocket.setReceiveTimeOut(timeout);
        }
	
	private int getMinPort(){
		port_range_min += 1;
		if (port_range_min>65535)
		{
			port_range_min = MIN_PORT_RANGE;
		}
		return port_range_min;
	}
	
	public void close(){
		pullsocket.close();
		zmqContext.term();
	}
	
	public String portStr(){
		return Integer.toString(pullSocketPort);
	}
	
	
	private List<byte[]> getPushPullResponse() throws OpenbciCommunicationException{
		byte[] response_header = pullsocket.recv();
		if (response_header == null)
			return null;
		byte[] response_data;
		if (pullsocket.hasReceiveMore()){
			response_data = pullsocket.recv();
			if (response_data == null)
				throw new OpenbciCommunicationException("Failed to get message data");
		}
		else
			throw new OpenbciCommunicationException("No message data is being sent");
		List<byte[]> response = new ArrayList();
		response.add(response_header);
		response.add(response_data);
		return response;
	}
	
	/**
	 * Use this after sending long obci request with getAdressLocal
	 * as client_push_address in the request Message to get answer.
	 * For example - to get available experiments.
	 * 
	 * @param msgType Type of the expected message if you get something else throws an exception
	 * @return Message on pull socket, expect it to by of msgType cast to base Message class.
	 * @throws OpenbciCommunicationException
	 */

	public LauncherMessage getAndParsePushPullResponse(MessageType msgType) throws OpenbciCommunicationException{
		List<byte[]> response = getPushPullResponse();
		if (response == null)
			return null;
		Helper.checkIfResponseIsOK(response, msgType);
		return (LauncherMessage)LauncherMessage.deserialize(response);
	}
	
	/*
	* We use Svarog to communicate with local obci server, which handles the
	* more complicated network discovery and communication for us.
	* That's why localhost should be enough.
	* Obci will send results of long requests to this pull adress,
	* using push socket.
	*/
	public String getAddressLocal(){
		return "tcp://localhost:" + portStr();
	}
}
