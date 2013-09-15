package worker.processmanagement;

import java.io.File;
import java.io.Serializable;

public class DoneMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5602519190517510697L;
	private int processID;
	private int clientID;

	public DoneMessage(int pid, int cid) {
		setProcessID(pid);
		setClientID(cid);
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}
}
