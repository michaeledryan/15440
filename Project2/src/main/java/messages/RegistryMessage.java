package messages;

import remote.MyRemote;

import java.io.Serializable;

/**
 */
public class RegistryMessage implements Serializable {

    private String host;
    private int port;
    private MessageType type;
    private String name;
    private MyRemote rref;

    private RegistryMessage(String host, int port, MessageType type,
                            String name) {
        this.host = host;
        this.port = port;
        this.type = type;
        this.name = name;
    }

    private RegistryMessage(MyRemote rref, MessageType type) {
        this.rref = rref;
        this.type = type;
    }

    public static RegistryMessage newRequest(String host, int port,
                                           String name) {
        return new RegistryMessage(host, port, MessageType.REQUEST, name);
    }

    public static RegistryMessage newReply(MyRemote rref) {
        return new RegistryMessage(rref, MessageType.REPLY);
    }

    public String getName() {
        return name;
    }

    public MyRemote getRref() {
        return rref;
    }

}
