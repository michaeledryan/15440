package worker.processmanagement;

public class ProcessControlMessage {

	private int processID;
	private ProcessControlCommand command;
	private String processLocation;

	public ProcessControlMessage(int id, ProcessControlCommand command, String processLocation) {
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
	
	public enum ProcessControlCommand {
		START, SUSPEND, MIGRATE;
	}
}
