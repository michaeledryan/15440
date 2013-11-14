package mikereduce.worker.shared;

import mikereduce.jobtracker.server.WorkerType;
import mikereduce.jobtracker.shared.JobClientStatus;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 7:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkerMessage implements Serializable{

    private WorkerStatus status;
    private JobStatus job;
    private WorkerType type;
    private int numCores;
    private int percent;

    private WorkerMessage(JobStatus job, int percent){
        this.job = job;
        this.percent = percent;
        this.status = WorkerStatus.UPDATE;
    }

    private WorkerMessage(WorkerType type, int numCores) {
        this.type = type;
        this.numCores = numCores;
        this.status = WorkerStatus.REGISTRATION;
    }

    public static WorkerMessage registration(WorkerType type, int numCores) {
        return new WorkerMessage(type, numCores);
    }

    public static WorkerMessage update(JobStatus job, int percent) {
        return new WorkerMessage(job, percent);
    }

    public WorkerType getType() {
        return type;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public JobStatus getJob() {
        return job;
    }

    public int getNumCores() {
        return numCores;
    }

    public int getPercent() {
        return percent;
    }
}