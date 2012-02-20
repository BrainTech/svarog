package org.signalml.app.worker.monitor.messages.parsing;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestErrorResponse;

import static org.signalml.app.util.i18n.SvarogI18n._;

public class MessageParser {

	public static Message parseMessageFromJSON(String json, MessageType messageType) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Message readMessage = (Message) mapper.readValue(json.getBytes(), messageType.getMessageClass());
			return readMessage;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean checkIfResponseIsOK(String response, MessageType awaitedMessageType) {
		if (response == null) {
			Dialogs.showError(_("Server is not responding!"));
			return false;
		}
		
		MessageType type = MessageType.parseMessageTypeFromResponse(response);
		if (type == awaitedMessageType) {
			return true;
		}
		else if (type == MessageType.REQUEST_ERROR_RESPONSE) {
			RequestErrorResponse msg = (RequestErrorResponse) MessageParser.parseMessageFromJSON(response, type);
			Dialogs.showError("Got request error from server (code: " + msg.getErrorCode() + ")");
			return false;
		}
		else {
			Dialogs.showError(_("Got unknown response from the server: ") + response);
			return false;
		}
	}
}
