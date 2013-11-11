package mikereduce.shared;

import mikereduce.jobtracker.shared.JobConfig;

import java.io.Serializable;

/**
 * Handles control of workers.
 */
public class WorkerControlMessage implements Serializable{

    private ControlMessageType type;
    private String jobName;
    private JobConfig config;

    public WorkerControlMessage(ControlMessageType type, String jobName, JobConfig conf) {
        this.type = type;
        this.jobName = jobName;
        this.config = conf;
    }

    public ControlMessageType getType() {
        return type;
    }

    public String getJobName() {
        return jobName;
    }

    public JobConfig getConfig() {
        return config;
    }
}
