package org.signalml.app.worker.monitor.zeromq;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using=MessageTypeSerializer.class)
@JsonDeserialize(using=MessageTypeDeserializer.class)
public enum MessageType {

	LIST_EXPERIMENTS,
	RUNNING_EXPERIMENTS,
	JOIN_EXPERIMENT;
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
