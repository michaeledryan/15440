package mikereduce.worker.shared;

import java.io.Serializable;

/**
 * Message sent from worker to JobTracker.
 */
public class WorkerMessage implements Serializable{

    private WorkerStatus status;
    private JobStatus job;
    private int numCores;
    private int percent;

    private WorkerMessage(JobStatus job, int percent){
        this.job = job;
        this.percent = percent;
        this.status = WorkerStatus.UPDATE;
    }

    private WorkerMessage(int numCores) {
        this.numCores = numCores;
        this.status = WorkerStatus.REGISTRATION;
    }

    private WorkerMessage() {
        this.status = WorkerStatus.HEARTBEAT;
    }

    public static WorkerMessage registration(int numCores) {
        return new WorkerMessage(numCores);
    }

    public static WorkerMessage update(JobStatus job, int percent) {
        return new WorkerMessage(job, percent);
    }

    public static WorkerMessage heartbeat() {
        return new WorkerMessage();
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