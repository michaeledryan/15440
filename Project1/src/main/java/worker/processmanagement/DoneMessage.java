package worker.processmanagement;

import java.io.Serializable;

public class DoneMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5602519190517510697L;
	public int processID;
	public int clientID;

	public DoneMessage(int pid, int cid) {
		processID = pid;
		clientID = cid;
	}
}
