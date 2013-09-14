package worker.processmanagement;

import java.io.Serializable;

public enum WorkerResponseType implements Serializable {
	PROCESS_FINISHED, PROCESS_SERIALIZED;

	public static WorkerResponseType fromString(String type) {
		return valueOf(type.toUpperCase());
	}
}
