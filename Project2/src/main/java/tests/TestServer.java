package tests;

import java.net.InetAddress;
import java.net.UnknownHostException;

import registry.Registry;
import remote.Remote440Exception;
import remote.RemoteObjectRef;
import server.ObjectTracker;
import server.RMIServer;
import tests.ints.RemoteInteger;
import tests.ints.RemoteIntegerImpl;
import tests.printer.RemotePrinter;
import tests.printer.RemotePrinterImpl;

public class TestServer {

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
				test2();
				break;
			}
			case "test2": {
				test2();
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

	private static void test1() throws UnknownHostException, Remote440Exception {
		RemoteObjectRef ror;
		ror = new RemoteObjectRef("toy0", InetAddress.getLocalHost()
				.getHostAddress(), server.getPort(),
				RemotePrinter.class.getName());
		registry.bind("toy0", ror, new RemotePrinterImpl());
		registry.rebind("toy0", ror, new RemotePrinterImpl());
	}

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

}
