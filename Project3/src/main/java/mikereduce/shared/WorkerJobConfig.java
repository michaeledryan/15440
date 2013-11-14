package mikereduce.shared;

import mikereduce.jobtracker.shared.JobConfig;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 */
public class WorkerJobConfig implements Serializable {

    private JobConfig conf;
    private InputBlock block;
    private String outputLocation;
    private int numReducers;
    private UUID jobId;

    public WorkerJobConfig(JobConfig conf, InputBlock block, String outputLocation, UUID jobId, int numReducers) {
        this.conf = conf;
        this.block = block;
        this.outputLocation = outputLocation;
        this.numReducers = numReducers;
        this.jobId = jobId;
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

}