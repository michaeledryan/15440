package registry;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FindRegistry {
	
	public static final int DEFAULT_RMI_PORT = 1099;

	public static RegistryImpl find(String host, int port) {
		//return new RegistryConnection(host, port);
		return null;
	}
	
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
