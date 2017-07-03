package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

public class FindEEGExperimentsRequest extends LongRequest {

	@JsonProperty("checked_srvs")
	private String checkedSrvs;
	
	@JsonProperty("only_local_exps")
	private boolean onlyLocalExps;

	public FindEEGExperimentsRequest(boolean local_exps) {
		super(MessageType.FIND_EEG_EXPERIMENTS_REQUEST);
		this.checkedSrvs = "";
		this.onlyLocalExps = local_exps;
	}
}
