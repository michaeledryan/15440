package util;

import java.io.Serializable;

public class RemoteObjectRef implements Serializable {

	private static final long serialVersionUID = 2360222867498946831L;
	private String name;
    private String host;
    private int port;

    public RemoteObjectRef(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

}
