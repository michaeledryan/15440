package AFS.nameserver;

import org.apache.commons.cli.*;

/**
 */
public class NameSever {

    static private String helpHeader = "Project 3: MR and AFS. 15-440, " +
            "Fall 2013. ";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    public static void main(String[] args) {

        Options opt = new Options();
        opt.addOption("p", "port", true,
                "Port to connect to registry. Default: 8000.");
        opt.addOption("h", "help", false, "Display help.");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("client", helpHeader, opt, helpFooter, true);
                System.exit(1);
            }
            System.out.println("Starting Name Server...");

            int port = 8000;
            try {
                port = Integer.parseInt(cmd.getOptionValue("p", "8000"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number.");
                System.exit(1);
            }

            new Listener(port).run();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
