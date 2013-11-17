package mikereduce.shared;

import mikereduce.jobtracker.server.JobPhase;
import mikereduce.jobtracker.shared.JobConfig;

import java.io.Serializable;
import java.util.UUID;

/**
 * Contains information from the JobTracker that tells a worker how to run
 */
public class WorkerJobConfig implements Serializable {

    private final int reducerIndex;
    private JobConfig conf;
    private InputBlock block;
    private String outputLocation;
    private int numReducers;
    private UUID jobId;
    private JobPhase phase;


    public WorkerJobConfig(JobConfig conf, InputBlock block, String outputLocation,
                           UUID jobId, int numReducers, int reducerIndex, JobPhase phase) {
        this.conf = conf;
        this.block = block;
        this.outputLocation = outputLocation;
        this.numReducers = numReducers;
        this.jobId = jobId;
        this.reducerIndex = reducerIndex;
        this.phase = phase;

    }

    public InputBlock getBlock() {
        return block;
    }

    public JobConfig getConf() {
        return conf;
    }

    public int getNumReducers() {
        return numReducers;
    }

    public String getOutputLocation() {
        return outputLocation;
    }


    public UUID getJobId() {
        return jobId;
    }

    public int getReducerIndex() {
        return reducerIndex;
    }

    public JobPhase getPhase() {
        return phase;
    }

}