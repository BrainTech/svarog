package org.signalml.app.worker.monitor.messages.parsing;

import static org.signalml.app.util.i18n.SvarogI18n._R;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestErrorResponse;

public class MessageParser {

	protected static final Logger logger = Logger.getLogger(MessageParser.class);

	public static Message parseMessageFromJSON(String json, MessageType messageType) throws OpenbciCommunicationException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Message readMessage = (Message) mapper.readValue(json.getBytes(), messageType.getMessageClass());
			return readMessage;
		} catch (Exception e) {
			logger.error("", e);
			throw new OpenbciCommunicationException(_R("An error occurred while parsing the JSON message ({0})", e.getStackTrace()[0]));
		}
	}

	public static void checkIfResponseIsOK(String response, MessageType awaitedMessageType) throws OpenbciCommunicationException {

		MessageType type = MessageType.parseMessageTypeFromResponse(response);
		if (type == awaitedMessageType) {
			//it's ok - do nothing
		}
		else if (type == MessageType.REQUEST_ERROR_RESPONSE) {
			RequestErrorResponse msg = (RequestErrorResponse) MessageParser.parseMessageFromJSON(response, type);
			throw new OpenbciCommunicationException(_R("Got request error from server (code: {0})", msg.getErrorCode()));
		}
		else {
			throw new OpenbciCommunicationException(_R("Got unexpected response from the server: {0}", response));
		}
	}
}
