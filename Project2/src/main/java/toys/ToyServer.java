package toys;

import server.ObjectTracker;
import server.RMIServer;

public class ToyServer {
	
	public static void main(String[] args) {
		RMIServer server = new RMIServer();
		server.startServer(args);
		ObjectTracker.getInstance().put("toy0", new ToyClassImpl());
		
	}
	
}
