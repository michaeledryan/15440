package mikereduce.worker.shared;

import mikereduce.jobtracker.shared.JobState;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
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
