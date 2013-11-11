package AFS.dataserver;

import AFS.nameserver.Listener;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.UUID;

/**
 */
public class DataServer {

    static private String helpHeader = "Project 3: MR and AFS. 15-440, " +
            "Fall 2013. ";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    public static void main(String[] args) {

        Options opt = new Options();
        opt.addOption("i", "identity", true,
                "Node identity. Default: randomized.");
        opt.addOption("p", "port", true,
                "Port to listen on. Default: 8000.");
        opt.addOption("n", "nameserver", true,
                "Nameserver host. Default: localhost.");
        opt.addOption("q", "nameserver-port", true,
                "Nameserver port. Default: 9000.");
        opt.addOption("h", "help", false, "Display help.");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("dataserver", helpHeader, opt, helpFooter,
                        true);
                System.exit(1);
            }
            System.out.println("Starting Data Server...");

            String id = "data" + File.separator +
                    cmd.getOptionValue("i", UUID.randomUUID().toString());
            String ns = cmd.getOptionValue("n", "localhost");
            int port = 8000;
            int np = 9000;
            try {
                port = Integer.parseInt(cmd.getOptionValue("p", "8000"));
                np = Integer.parseInt(cmd.getOptionValue("q", "9000"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number.");
                System.exit(1);
            }

            new DataHandler(id, port, ns, np).run();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
