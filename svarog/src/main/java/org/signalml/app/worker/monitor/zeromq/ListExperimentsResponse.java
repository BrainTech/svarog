package org.signalml.app.worker.monitor.zeromq;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class ListExperimentsResponse extends Message {

	@JsonIgnore
	//@JsonProperty("exp_data")
	private String exp_data;

	public String getExp_data() {
		return exp_data;
	}

	public void setExp_data(String exp_data) {
		this.exp_data = exp_data;
	}
	
	
}
