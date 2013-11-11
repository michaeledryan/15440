package mikereduce.worker.shared;

import mikereduce.jobtracker.shared.JobState;

import java.io.Serializable;

/**
 * Contains information about a specific job.
 */
public class JobStatus implements Serializable{

    private JobState state;

    private String jobName; // What do we use for job IDs?

    public JobStatus(JobState state, String jobName) {
        this.state = state;
        this.jobName = jobName;
    }

    public JobState getState() {
        return state;
    }

    public String getJobName() {
        return jobName;
    }
}
