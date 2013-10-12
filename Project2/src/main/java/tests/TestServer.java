package tests;

import java.net.InetAddress;
import java.net.UnknownHostException;

import registry.Registry;
import remote.Remote440Exception;
import remote.RemoteObjectRef;
import server.RMIServer;
import tests.ints.RemoteInteger;
import tests.ints.RemoteIntegerImpl;
import tests.printer.RemotePrinter;
import tests.printer.RemotePrinterImpl;

/**
 * Example implementation of a server used to showcase tests. Like the client,
 * simply switches on the trace passed in.
 * 
 * @author Alex Cappiello and Michael Ryan
 * 
 */
public class TestServer {

	private static Registry registry;
	private static RMIServer server;

	public static void main(String[] args) {
		// Plug in to the framework.
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
                case "test2": {
                    test2();
                    break;
                }
                case "test3": {
                    test3();
                    break;
                }
                case "test3b": {
                    test3b();
                    break;
                }
                case "test4": {
                    test4();
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

	}

	/**
	 * Very simple case. Generate one reference, bind, and unbind.
	 * 
	 * @throws UnknownHostException
	 * @throws Remote440Exception
	 */
	private static void test1() throws UnknownHostException, Remote440Exception {
		RemoteObjectRef ror;
		ror = new RemoteObjectRef("toy0", InetAddress.getLocalHost()
				.getHostAddress(), server.getPort(),
				RemotePrinter.class.getName());
		registry.bind("toy0", ror, new RemotePrinterImpl());
		registry.rebind("toy0", ror, new RemotePrinterImpl());
	}

	/**
	 * More interesting case. 
	 * @throws UnknownHostException
	 * @throws Remote440Exception
	 */
	private static void test2() throws UnknownHostException, Remote440Exception {
		RemoteObjectRef ror1 = new RemoteObjectRef("int1", InetAddress
				.getLocalHost().getHostAddress(), server.getPort(),
				RemoteInteger.class.getName());

		RemoteObjectRef ror2 = new RemoteObjectRef("int1", InetAddress
				.getLocalHost().getHostAddress(), server.getPort(),
				RemoteInteger.class.getName());

		RemoteObjectRef ror3 = new RemoteObjectRef("int1", InetAddress
				.getLocalHost().getHostAddress(), server.getPort(),
				RemoteInteger.class.getName());

		RemoteObjectRef ror4 = new RemoteObjectRef("int1", InetAddress
				.getLocalHost().getHostAddress(), server.getPort(),
				RemoteInteger.class.getName());

		RemoteObjectRef ror5 = new RemoteObjectRef("int1", InetAddress
				.getLocalHost().getHostAddress(), server.getPort(),
				RemoteInteger.class.getName());

		registry.bind("int1", ror1, new RemoteIntegerImpl(1));
		registry.bind("int2", ror2, new RemoteIntegerImpl(2));
		registry.bind("int3", ror3, new RemoteIntegerImpl(3));
		registry.bind("int4", ror4, new RemoteIntegerImpl(4));
		registry.bind("int5", ror5, new RemoteIntegerImpl(5));

	}

    private static void test3() throws UnknownHostException,
            Remote440Exception {
        RemoteObjectRef ror1 = new RemoteObjectRef("int1", InetAddress
                .getLocalHost().getHostAddress(), server.getPort(),
                RemoteInteger.class.getName());

        RemoteObjectRef ror2 = new RemoteObjectRef("int1", InetAddress
                .getLocalHost().getHostAddress(), server.getPort(),
                RemoteInteger.class.getName());

        registry.bind("int1", ror1, new RemoteIntegerImpl(1));
        registry.bind("int2", ror2, new RemoteIntegerImpl(2));
    }

    private static void test3b() throws UnknownHostException,
            Remote440Exception {
        RemoteObjectRef ror1 = new RemoteObjectRef("int1", InetAddress
                .getLocalHost().getHostAddress(), server.getPort(),
                RemoteInteger.class.getName());

        RemoteObjectRef ror2 = new RemoteObjectRef("int1", InetAddress
                .getLocalHost().getHostAddress(), server.getPort(),
                RemoteInteger.class.getName());

        registry.bind("int1", ror1, new RemoteIntegerImpl(3));
        registry.bind("int2", ror2, new RemoteIntegerImpl(4));
    }

    private static void test4() throws UnknownHostException,
            Remote440Exception {
        RemoteObjectRef ror = new RemoteObjectRef("toy0", InetAddress
                .getLocalHost().getHostAddress(), server.getPort(),
                RemotePrinter.class.getName());

        RemoteObjectRef ror1 = new RemoteObjectRef("int1", InetAddress
                .getLocalHost().getHostAddress(), server.getPort(),
                RemoteInteger.class.getName());

        RemoteObjectRef ror2 = new RemoteObjectRef("int1", InetAddress
                .getLocalHost().getHostAddress(), server.getPort(),
                RemoteInteger.class.getName());

        registry.bind("int1", ror1, new RemoteIntegerImpl(1));
        registry.bind("int2", ror2, new RemoteIntegerImpl(2));
        registry.bind("toy0", ror, new RemotePrinterImpl());
    }

}
