package common;

import java.io.Serializable;

/**
 * Request originates in a client and is sent to the master.
 * 
 * @author acappiel
 * 
 */
public class ClientRequest implements Serializable {

	private static final long serialVersionUID = -5177348684388096883L;
	private String request;
	private ClientRequestType type;
	private int processId;
	private int clientId;

	/**
	 * Create request.
	 * 
	 * @param cid
	 *            Although client initially assigns a value, master ignores and
	 *            assigns its own.
	 * @param pid
	 *            Assigned by client.
	 * @param request
	 *            Command to process by the master. This may be a new process or
	 *            a control command.
	 */
	public ClientRequest(String request, ClientRequestType type) {
		this.setProcessId(0);
		this.setRequest(request);
		this.setType(type);
	}

	public ClientRequest(int pid, String request, ClientRequestType type) {
		this.setProcessId(pid);
		this.setRequest(request);
		this.setType(type);
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public ClientRequestType getType() {
		return type;
	}

	public void setType(ClientRequestType type) {
		this.type = type;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int uuid) {
		clientId = uuid;
	}

}