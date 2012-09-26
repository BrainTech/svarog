package org.signalml.app.worker.monitor.messages;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class GetExperimentContactResponse extends Message {

	@JsonProperty("rep_addrs")
	private Object repAddress;

	@JsonProperty("status_name")
	private String statusName;

	@JsonProperty("pub_addrs")
	private Object pubAddress;
	private String name;
	private String machine;

	@JsonProperty("tcp_addrs")
	private List<List<Object>> tcpAddress;
	private Object details;
	private String uuid;

	public GetExperimentContactResponse() {
		super(MessageType.GET_EXPERIMENT_CONTACT_RESPONSE);
	}

	public String getExperimentIPAddress() {
		return (String) tcpAddress.get(0).get(0);
	}

	public Integer getExperimentPort() {
		return (Integer) tcpAddress.get(0).get(1);
	}

	public Object getRepAddress() {
		return repAddress;
	}

	public void setRepAddress(Object repAddress) {
		this.repAddress = repAddress;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public Object getPubAddress() {
		return pubAddress;
	}

	public void setPubAddress(Object pubAddress) {
		this.pubAddress = pubAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public List<List<Object>> getTcpAddress() {
		return tcpAddress;
	}
	public void setTcpAddress(List<List<Object>> tcpAddress) {
		this.tcpAddress = tcpAddress;
	}

	public Object getDetails() {
		return details;
	}

	public void setDetails(Object details) {
		this.details = details;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
