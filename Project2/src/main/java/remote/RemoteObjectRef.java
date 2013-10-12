package remote;

import java.io.Serializable;

/**
 * Reference to an object stored in a remote RMI registry.
 * This is stored in each stub class for communication with the
 * actual object.
 *
 * @author Michael Ryan and Alex Capiello
 */
public class RemoteObjectRef implements Serializable, Remote440 {

    private static final long serialVersionUID = 2360222867498946831L;
    private String name; // Unique identifier for the remote object.
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

    /**
     * Returns a stub of the class specified in interfaceName, then adds itself
     * to the stub so that we can use the data in the reference to access the
     * remote object.
     */
    public Object localize() throws ClassNotFoundException {
        String stubClassName = interfaceName + "Stub";

        Class<?> stubClass = Class.forName(stubClassName);

        RemoteStub result = null;

        try {
            result = (RemoteStub) stubClass.newInstance();
            result.setRemoteRef(this);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;

    }
}
