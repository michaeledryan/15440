package mikereduce.shared;

import java.io.Serializable;

/**
 * Handles control of workers.
 */
public class WorkerControlMessage implements Serializable {

    private ControlMessageType type;
    private WorkerJobConfig config;
    private final String addr;
    private final int port;


    public WorkerControlMessage(ControlMessageType type, WorkerJobConfig conf, String address, int port) {
        this.type = type;
        this.config = conf;
        this.addr = address;
        this.port = port;
    }

    public ControlMessageType getType() {
        return type;
    }

    public WorkerJobConfig getConfig() {
        return config;
    }

    public int getFSPort() {
        return port;
    }

    public String getFSHost() {
        return addr;
    }
}
