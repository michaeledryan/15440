package messages;

import remote.Remote440;

import java.io.Serializable;

/**
 */
public class RegistryMessage implements Serializable {

    private String host;
    private int port;
    private MessageType type;
    private String name;
    private Remote440 rref;

    private RegistryMessage(String host, int port, MessageType type,
                            String name) {
        this.host = host;
        this.port = port;
        this.type = type;
        this.name = name;
    }

    private RegistryMessage(Remote440 rref, MessageType type) {
        this.rref = rref;
        this.type = type;
    }

    public static RegistryMessage newRequest(String host, int port,
                                           String name) {
        return new RegistryMessage(host, port, MessageType.REQUEST, name);
    }

    public static RegistryMessage newReply(Remote440 rref) {
        return new RegistryMessage(rref, MessageType.REPLY);
    }

    public String getName() {
        return name;
    }

    public Remote440 getRref() {
        return rref;
    }

}
