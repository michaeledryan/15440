package mikereduce.shared;

import mikereduce.jobtracker.shared.JobConfig;

import java.io.Serializable;

/**
 * Handles control of workers.
 */
public class WorkerControlMessage implements Serializable{

    private ControlMessageType type;
    private WorkerJobConfig config;

    public WorkerControlMessage(ControlMessageType type, WorkerJobConfig conf) {
        this.type = type;
        this.config = conf;
    }

    public ControlMessageType getType() {
        return type;
    }

    public WorkerJobConfig getConfig() {
        return config;
    }
}
