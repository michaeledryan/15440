package client;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import registry.RegistryProxy;

/**
 * The client runs predefined test traces. The location of the registries are
 * given through the CLI.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class Client {

    static private String helpHeader = "Project 2: RMI. 15-440, Fall 2013. " +
            "-r and -p and be given multiple times.";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    private RegistryProxy[] registries;
    private String trace;

    public void startClient(String[] args) {

        Options opt = new Options();
        opt.addOption("r", "registry", true,
                "Hostname of registry. Default: localhost.");
        opt.addOption("p", "port", true,
                "Port to connect to registry. Default: 8000.");
        opt.addOption("t", "trace", true,
                "Trace to run internally. Default: test1.");
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
            trace = cmd.getOptionValue("t", "test1");

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

            

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public RegistryProxy[] getRegistries() {
        return registries;
    }

    public String getTrace() {
        return trace;
    }
    
}
