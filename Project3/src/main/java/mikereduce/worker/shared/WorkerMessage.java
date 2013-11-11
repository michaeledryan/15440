package mikereduce.worker.shared;

import mikereduce.jobtracker.server.WorkerType;
import mikereduce.jobtracker.shared.JobClientStatus;

import java.io.Serializable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 7:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkerMessage implements Serializable{

    private WorkerStatus status;
    private Set<JobStatus> jobs;
    private WorkerType type;

    public WorkerMessage(WorkerType type, Set<JobStatus> jobs, WorkerStatus status) {
        this.type = type;
        this.jobs = jobs;
        this.status = status;
    }

    public WorkerType getType() {
        return type;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public Set<JobStatus> getJobs() {
        return jobs;
    }

}