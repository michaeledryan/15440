package worker.processmanagement;

import java.io.File;
import java.io.Serializable;

/**
 * Object wrapping messages back from workers to the master.
 * 
 * @author michaelryan
 * 
 */
public class WorkerResponse implements Serializable {

	private static final long serialVersionUID = -5602519190517510697L;
	private int processID;
	private int clientID;
	private File serializedFile = null;
	private WorkerResponseType type;

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public File getSerializedFile() {
		return serializedFile;
	}

	public void setSerializedFile(File serializedFile) {
		this.serializedFile = serializedFile;
	}

	public WorkerResponseType getType() {
		return type;
	}

	public void setType(WorkerResponseType type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public WorkerResponse(int pid, int cid) {
		this.processID = pid;
		this.clientID = cid;
		this.type = WorkerResponseType.PROCESS_FINISHED;
	}

	public WorkerResponse(int pid, int cid, File serializedFile) {
		this.processID = pid;
		this.serializedFile = serializedFile;
		this.clientID = cid;
		this.type = WorkerResponseType.PROCESS_SERIALIZED;
	}
}
