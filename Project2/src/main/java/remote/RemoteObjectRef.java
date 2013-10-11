package remote;

public class RemoteObjectRef implements MyRemote {

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
