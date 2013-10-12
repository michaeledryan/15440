package toys;

import java.net.InetAddress;
import java.net.UnknownHostException;

import registry.Registry;
import remote.Remote440Exception;
import remote.RemoteObjectRef;
import server.ObjectTracker;
import server.RMIServer;

public class ToyServer {

	public static void main(String[] args) {
		RMIServer server = new RMIServer();
		server.startServer(args);
		Registry registry = server.getRegistry();
		
		
		RemoteObjectRef ror = null;
		try {
			ror = new RemoteObjectRef("toy0", InetAddress.getLocalHost().getHostAddress(), server.getPort(), ToyClass.class.getName());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			System.out.println("before");
			registry.bind("toy0", ror);
			System.out.println("after");
			System.out.println(registry.list());
		} catch (Remote440Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		ObjectTracker.getInstance().put("toy0", new ToyClassImpl());
		
	}
}
