package client;

import org.apache.commons.cli.*;

import registry.Registry;
import registry.RegistryProxy;
import remote.Remote440Exception;
import tests.printer.RemotePrinter;

/**
 * The client runs predefined test traces. The location of the registries are
 * given through the CLI.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class Client {

    static private String helpHeader = "Project 2: RMI. 15-440, Fall 2013.";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    static private RegistryProxy[] registries;

    public static void main(String[] args) {

        Options opt = new Options();
        opt.addOption("r", "registry", true,
                "Hostname of registry. Default: localhost.");
        opt.addOption("p", "port", true, "Port to connect to. Default: 8000.");
        opt.addOption("t", "trace", true, "Trace to run internally.");
        opt.addOption("h", "help", false, "Display help.");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("client", helpHeader, opt, helpFooter, true);
                System.exit(1);
            }
            System.out.println("Starting client...");
            String trace = cmd.getOptionValue("t", "test1");

            // CLI parsing gets a little ugly, since we have multiple
            // registries.
            String[] regs = cmd.getOptionValues("r");
            String[] ports = cmd.getOptionValues("p");
            if (regs == null) {
                regs = new String[1];
                regs[0] = "localhost";
            }
            if (ports != null && regs.length != ports.length) {
                System.err.println("Error: Must either specify ports for all " +
                        "registries or none (if all are default).");
                System.exit(1);
            }

            registries = new RegistryProxy[regs.length];

            for (int i = 0; i < regs.length; i++) {
                String reg = regs[i];
                int port = 8000;
                if (ports != null) {
                    String portString = ports[i];
                    try {
                        port = Integer.parseInt(portString);
                    } catch (NumberFormatException e) {
                        System.err.printf("Invalid port number: %s\n",
                                portString);
                        System.exit(1);
                    }
                }
                registries[i] = new RegistryProxy(reg, port);
            }

            String word = registries.length > 1 ? "Registries" : "Registry";
            System.out.printf("Connected to %d %s\n",
                    registries.length, word);

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

        } catch (ParseException e) {
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
