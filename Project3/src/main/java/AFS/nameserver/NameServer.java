package AFS.nameserver;

import org.apache.commons.cli.*;

/**
 * Entry point for the nameserver.
 */
public class NameServer {

    static private String helpHeader = "Project 3: MR and AFS. 15-440, " +
            "Fall 2013. ";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    public static void main(String[] args) {

        Options opt = new Options();
        opt.addOption("n", "nodes", true,
                "Number of data nodes. Default: 2.");
        opt.addOption("p", "port", true,
                "Port to listen on. Default: 8000.");
        opt.addOption("r", "replication", true,
                "Replication factor. Default: 2.");
        opt.addOption("h", "help", false, "Display help.");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("nameserver", helpHeader, opt, helpFooter,
                        true);
                System.exit(1);
            }
            System.out.println("Starting Name Server...");

            int nodes = 2;
            int rep = 2;
            int port = 9000;
            try {
                nodes = Integer.parseInt(cmd.getOptionValue("n", "2"));
                port = Integer.parseInt(cmd.getOptionValue("p", "9000"));
                rep = Integer.parseInt(cmd.getOptionValue("r", "2"));
            } catch (NumberFormatException e) {
                System.err.println("Input not parsable as int.");
                System.exit(1);
            }

            FileMap.getInstance().setReplication(rep);
            new Listener(port, nodes).run();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
