package registry;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Connect to the registry.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class FindRegistry {

    public static final int DEFAULT_RMI_PORT = 1099;

    /**
     * Connect to a specific registry.
     *
     * @param host The host to connect to.
     * @param port The port.
     * @return The registry.
     */
    public static RegistryImpl find(String host, int port) {
        //return new RegistryConnection(host, port);
        return null;
    }

    /**
     * Don't know where to look? Try the default location!
     *
     * @return The registry.
     */
    public static RegistryImpl find() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // Let's try our best.
            host = "";
        }
        return find(host, DEFAULT_RMI_PORT);
    }


}
