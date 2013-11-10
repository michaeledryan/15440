package mikereduce.worker.shared;

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

    public WorkerMessage(WorkerStatus status, Set<JobStatus> jobs) {
        this.status = status;
        this.jobs = jobs;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public Set<JobStatus> getJobs() {
        return jobs;
    }

}