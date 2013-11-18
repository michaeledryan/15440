package AFS.management;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.ConnectException;

/**
 * CLI for AFS management.
 */
public class Management {

    static private String helpHeader = "Project 3: MR and AFS. 15-440, " +
            "Fall 2013. ";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    public static void main(String[] args) {

        Options opt = new Options();
        opt.addOption("n", "hostname", true,
                "Hostname to manage. Default: localhost.");
        opt.addOption("p", "port", true,
                "Port to connect to. Default: 9000.");
        opt.addOption("h", "help", false, "Display help.");

        String host = "localhost";
        int port = 9000;

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("query", helpHeader, opt, helpFooter,
                        true);
                System.exit(1);
            }

            try {
                port = Integer.parseInt(cmd.getOptionValue("p", "9000"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number.");
                System.exit(1);
            }

            host = cmd.getOptionValue("n", "localhost");

            String[] input = cmd.getArgs();
            String type = null;
            String arg = null;

            switch (input.length) {
                case 2:
                    arg = input[1];
                case 1:
                    type = input[0];
                    break;
                default:
                    System.err.println("Bad query.");
                    System.err.println("usage: dataserver [-h] [-n <arg>] " +
                            "+[-p <arg>] <FILES | NODES>");
                    System.exit(1);
            }

            ToolImpl t = new ToolImpl(host, port);
            t.query(type, arg);

        } catch (ConnectException e) {
            System.err.printf("Node does not appear to be running: %s:%d\n",
                    host, port);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

}
