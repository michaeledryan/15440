package worker.processmanagement;

import java.io.Serializable;

/**
 * Object wrapping control messages to Workers. Suspends a running process or
 * restarts a suspended process.
 * 
 * @author michaelryan
 * 
 */
public class ProcessControlMessage implements Serializable {

	private static final long serialVersionUID = -3809804617000219594L;
	private int processID;
	private ProcessControlCommand command;
	private String processLocation;

	public ProcessControlMessage(int id, ProcessControlCommand command,
			String processLocation) {
		this.processID = id;
		this.command = command;
		this.processLocation = processLocation;
	}

	public int getProcessID() {
		return processID;
	}

	public ProcessControlCommand getCommand() {
		return command;
	}

	public String getProcessLocation() {
		return processLocation;
	}

	/**
	 * Enum for control message types. Options include start, suspend, and
	 * migrate.
	 * 
	 * Start: Fire up a process that was serialized to the given location.
	 * Suspend: Stop a process and serialize it to a file.
	 * 
	 * @author michaelryan
	 * 
	 */
	public enum ProcessControlCommand {
		START, MIGRATE, RESTART, KILLALL;
	}
}
