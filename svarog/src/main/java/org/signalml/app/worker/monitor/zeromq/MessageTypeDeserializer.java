package org.signalml.app.worker.monitor.zeromq;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class MessageTypeDeserializer extends JsonDeserializer<MessageType> {

	@Override
	public MessageType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		
		String text = parser.getText();
		text = text.toUpperCase();

		return MessageType.valueOf(text);
	}

}
