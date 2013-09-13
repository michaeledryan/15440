package common;

import java.io.Serializable;
import java.util.Random;

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
	private int clientId;
	private int processId;

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
	public ClientRequest(int cid, int pid, String request, ClientRequestType type) {
		this.setClientId(cid);
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

	public ClientRequestType getType() {
		return type;
	}

	public void setType(ClientRequestType type) {
		this.type = type;
	}

}
