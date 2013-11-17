package mikereduce.worker.shared;

/**
 * Enum for message types sent by workers to the JobTracker
 */
public enum WorkerStatus {
    REGISTRATION, HEARTBEAT, UPDATE, ERROR;
}
