package client;

import org.apache.commons.cli.*;

/**
 */
public class Client {

    static private String helpHeader =
            "Project 2: RMI. 15-440, Fall 2013.";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    private static String registry;
    private static int port;

    public static void main(String[] args) {

        Options opt = new Options();
        opt.addOption("r", "registry", true,
                "Hostname of registry. Default: localhost.");
        opt.addOption("p", "port", true, "Port to connect to. Default: 8000.");
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
            String portString = cmd.getOptionValue("p", "8000");
            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                System.err.printf("Invalid port number: %s\n", portString);
                System.exit(1);
            }
            registry = cmd.getOptionValue("r", "localhost");
            // DO SOMETHING.
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
