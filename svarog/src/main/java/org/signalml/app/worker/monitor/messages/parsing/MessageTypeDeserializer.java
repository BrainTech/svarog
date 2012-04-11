package org.signalml.app.worker.monitor.messages.parsing;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.signalml.app.worker.monitor.messages.MessageType;

public class MessageTypeDeserializer extends JsonDeserializer<MessageType> {

	@Override
	public MessageType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		String code = parser.getText();
		return MessageType.parseMessageTypeFromMessageCode(code);
	}

}
