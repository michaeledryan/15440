package toys;

import java.net.InetAddress;
import java.net.UnknownHostException;

import registry.Registry;
import remote.Remote440Exception;
import remote.RemoteObjectRef;
import server.ObjectTracker;
import server.RMIServer;

public class ToyServer {

    private static Registry registry;
    private static RMIServer server;

    public static void main(String[] args) {
        server = new RMIServer();
        server.startServer(args);
        registry = server.getRegistry();
        String trace = server.getTrace();

        System.out.printf("Running trace: %s\n", trace);
        try {
            switch (trace) {
                case "test1": {
                    test1();
                    break;
                }
                default: {
                    System.err.printf("Unknown trace: %s\n", trace);
                    System.exit(1);
                }
            }
        } catch (Remote440Exception | UnknownHostException e) {
            e.printStackTrace();
        }

        ObjectTracker.getInstance().put("toy0", new ToyClassImpl());

    }

    private static void test1() throws UnknownHostException,
            Remote440Exception {
        RemoteObjectRef ror;
        ror = new RemoteObjectRef("toy0",
                InetAddress.getLocalHost().getHostAddress(),
                server.getPort(), ToyClass.class.getName());
        registry.bind("toy0", ror);
        registry.rebind("toy0", ror);
    }
}
