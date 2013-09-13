package common;

import java.io.Serializable;
import java.util.Random;

public class ClientRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5177348684388096883L;
	private String request;
	private int clientId;
	private int processId;

	public ClientRequest(int cid, int pid, String request) {
		this.setClientId(cid);
		this.setProcessId(pid);
		this.setRequest(request);
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

}
