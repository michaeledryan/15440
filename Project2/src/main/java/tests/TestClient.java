package tests;

import client.Client;
import registry.Registry;
import registry.RegistryProxy;
import remote.Remote440Exception;
import tests.printer.RemotePrinter;

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
            toy.printMessage("SHIT");
            toy.printMessage("FOOBAR");
        }

    }

}
