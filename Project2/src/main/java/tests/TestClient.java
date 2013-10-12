package tests;

import registry.Registry;
import registry.RegistryProxy;
import remote.Remote440Exception;
import tests.ints.RemoteInteger;
import tests.printer.RemotePrinter;
import client.Client;

/**
 */
public class TestClient {

	private static Client client;
	private static RegistryProxy[] registries;

	public static void main(String[] args) {

		client = new Client();
		client.startClient(args);
		registries = client.getRegistries();
		String trace = client.getTrace();

		System.out.printf("Running trace: %s\n", trace);

		// Pick the correct trace.
		// Client and server should be on the same trace.
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
                case "test4": {
                    test4();
                    break;
                }
                default: {
                    System.err.printf("Unknown trace: %s\n", trace);
                    System.exit(1);
                }
            }
		} catch (Remote440Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Really simple test on the ToyClass.
	 *
	 * @throws Remote440Exception
	 */
	private static void test1() throws Remote440Exception {
		Registry proxy = registries[0];
		String item = "toy0";
		System.out.printf("Looking up: %s\n", item);
		RemotePrinter toy = (RemotePrinter) proxy.lookup(item);
		if (toy != null) {
			toy.printMessage("FOO");
			toy.printMessage("BAR");
		}

	}

	private static void test2() throws Remote440Exception {
		Registry proxy = registries[0];
		String item1 = "int1";
		String item2 = "int2";
		String item3 = "int3";
		String item4 = "int4";
		String item5 = "int5";
		System.out.println("Looking up items int1 - int5.");
		RemoteInteger i1 = (RemoteInteger) proxy.lookup(item1);
		RemoteInteger i2 = (RemoteInteger) proxy.lookup(item2);
		RemoteInteger i3 = (RemoteInteger) proxy.lookup(item3);
		RemoteInteger i4 = (RemoteInteger) proxy.lookup(item4);
		RemoteInteger i5 = (RemoteInteger) proxy.lookup(item5);

		System.out.println("Value of i1 before sum: " + i1.getValue());

		RemoteInteger[] addends = { i1, i2, i3, i4, i5 };
		i1.destructiveSum(addends);
		System.out.println("Value of i1 after sum: " + i1.getValue());

		i1.destructiveAdd(i1);

		System.out.println("Value of i1 after sum and add: " + i1.getValue());

	}

    private static void test3() throws Remote440Exception {
        assert(registries.length >= 2);
        Registry proxy1 = registries[0];
        Registry proxy2 = registries[1];
        String item1 = "int1";
        String item2 = "int2";

        System.out.println("Looking up items int1 - int4.");
        RemoteInteger i1 = (RemoteInteger) proxy1.lookup(item1);
        RemoteInteger i2 = (RemoteInteger) proxy1.lookup(item2);
        RemoteInteger i3 = (RemoteInteger) proxy2.lookup(item1);
        RemoteInteger i4 = (RemoteInteger) proxy2.lookup(item2);

        System.out.println("Value of i1 before sum: " + i1.getValue());

        RemoteInteger[] addends = { i1, i2, i3, i4 };
        i1.destructiveSum(addends);
        System.out.println("Value of i1 after sum: " + i1.getValue());

        i1.destructiveAdd(i1);

        System.out.println("Value of i1 after sum and add: " + i1.getValue());
    }

    private static void test4() throws Remote440Exception {
        Registry proxy = registries[0];

        String item1 = "int1";
        String item2 = "int2";
        String item3 = "toy0";

        System.out.println("Looking up items int1 - int2.");
        RemoteInteger i1 = (RemoteInteger) proxy.lookup(item1);
        RemoteInteger i2 = (RemoteInteger) proxy.lookup(item2);
        RemotePrinter p1 = (RemotePrinter) proxy.lookup(item3);

        System.out.println("Adding i1 to i2");
        i1.destructiveAdd(i2);

        System.out.println("Result: " + i1.getValue());

        System.out.println("Now for a RemotePrinter");
        System.out.println(p1.printMessage("Hello there."));
    }

}
