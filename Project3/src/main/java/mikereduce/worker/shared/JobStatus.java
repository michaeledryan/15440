package mikereduce.worker.shared;

import mikereduce.jobtracker.shared.JobState;

import java.io.Serializable;
import java.util.UUID;

/**
 * Contains information about a specific job.
 */
public class JobStatus implements Serializable {

    private JobState state;

    private UUID id; // What do we use for job IDs?

    public JobStatus(JobState state, UUID jobId) {
        this.state = state;
        this.id = jobId;
    }

    public JobState getState() {
        return state;
    }

    public UUID getId() {
        return id;
    }

}
