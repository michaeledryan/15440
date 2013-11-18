package mikereduce.jobtracker.server;

import mikereduce.jobtracker.shared.JobConfig;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/11/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClientMessage implements Serializable {

    private ClientMessageType type;

    private JobConfig conf;

    public ClientMessage(ClientMessageType type, JobConfig conf) {
        this.type = type;
        this.conf = conf;
    }

    public ClientMessage(ClientMessageType type) {
        this.type = type;
    }

    public JobConfig getConf() {
        return conf;
    }

    public ClientMessageType getType() {
        return type;
    }

    public void setConf(JobConfig conf) {
        this.conf = conf;

    }
}
