package server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import toys.ToyClassImpl;

public class Server {

    static private String helpHeader =
            "Project 2: RMI. 15-440, Fall 2013.";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    private static int port;

    public static void main(String[] args) {

        Options opt = new Options();
        opt.addOption("p", "port", true, "Port to listen on. Default: 1099.");
        opt.addOption("h", "help", false, "Display help.");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("server", helpHeader, opt, helpFooter, true);
                System.exit(1);
            }
            System.out.println("Starting server...");
            String portString = cmd.getOptionValue("p", "1099");
            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                System.err.printf("Invalid port number: %s\n", portString);
                System.exit(1);
            }
            Listener l = new Listener(port);
            ObjectTracker.getInstance().put("toy0", new ToyClassImpl());
            l.run();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
