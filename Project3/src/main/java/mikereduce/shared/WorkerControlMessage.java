package mikereduce.shared;

import mikereduce.jobtracker.shared.JobConfig;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkerControlMessage {

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
