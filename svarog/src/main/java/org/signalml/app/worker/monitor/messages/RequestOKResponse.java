package org.signalml.app.worker.monitor.messages;

import java.util.LinkedHashMap;

public class RequestOKResponse extends LauncherMessage {

	private LinkedHashMap<String, Object> params;
	private String status;
	private String request;
	private String details;

	public LinkedHashMap<String, Object> getParams() {
		return params;
	}
	
	public void setParams(LinkedHashMap<String, Object> params) {
		this.params = params;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
