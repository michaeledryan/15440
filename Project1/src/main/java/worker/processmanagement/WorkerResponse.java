package worker.processmanagement;

import java.io.File;
import java.io.Serializable;

public class WorkerResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5602519190517510697L;
	public int processID;
	public int clientID;
	public File serializedFile;
	public WorkerResponseType type;

	public WorkerResponse(int pid, int cid) {
		this.processID = pid;
		this.clientID = cid;
		this.type = WorkerResponseType.PROCESS_FINISHED;
	}

	public WorkerResponse(int pid, File serializedFile) {
		this.processID = pid;
		this.serializedFile = serializedFile;
		this.type = WorkerResponseType.PROCESS_SERIALIZED;
	}
}
