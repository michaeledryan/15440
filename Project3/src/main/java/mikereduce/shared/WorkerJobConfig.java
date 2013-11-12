package mikereduce.shared;

import mikereduce.jobtracker.shared.JobConfig;

/**
 *
 */
public class WorkerJobConfig {

    private JobConfig conf;
    private InputBlock block;
    private String outputLocation;

    public WorkerJobConfig(JobConfig conf, InputBlock block, String outputLocation) {
        this.conf = conf;
        this.block = block;
        this.outputLocation = outputLocation;
    }

    public InputBlock getBlock() {
        return block;
    }

    public JobConfig getConf() {
        return conf;
    }
}