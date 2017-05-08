package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class RequestErrorResponse extends LauncherMessage {

	private String details;

	@JsonProperty("err_code")
	private String errorCode;

	@JsonIgnore
	private String request;

	public RequestErrorResponse() {
		super(MessageType.REQUEST_ERROR_RESPONSE);
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

}
