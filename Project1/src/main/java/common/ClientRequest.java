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
	private int taskId;

	public ClientRequest(int id, String request) {
		this.setClientId(id);
		this.setRequest(request);
		this.setTaskId(new Random().nextInt());
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

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

}
