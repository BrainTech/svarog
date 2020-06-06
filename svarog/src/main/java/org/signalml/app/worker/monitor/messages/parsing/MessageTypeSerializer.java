package org.signalml.app.worker.monitor.messages.parsing;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.signalml.app.worker.monitor.messages.MessageType;

public class MessageTypeSerializer extends JsonSerializer<MessageType> {

	@Override
	public void serialize(MessageType messageType, JsonGenerator jgen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		jgen.writeString(messageType.toString());
	}

}
