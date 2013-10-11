package remote;

import java.io.Serializable;

public class RemoteObjectRef implements Serializable, Remote440 {

    private static final long serialVersionUID = 2360222867498946831L;
    private String name;
    private String host;
    private int port;
    private String interfaceName;

    public RemoteObjectRef(String name, String host, int port,
            String interfaceName) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.interfaceName = interfaceName;
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

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Object localize() throws ClassNotFoundException {
        String stubClassName = interfaceName + "Stub";

        System.out.println(stubClassName);
        Class<?> stubClass = Class.forName(stubClassName);

        RemoteStub result = null;

        try {
            result = (RemoteStub) stubClass.newInstance();
            result.setRemoteRef(this);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }
}
