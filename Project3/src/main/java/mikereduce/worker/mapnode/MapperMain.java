package mikereduce.worker.mapnode;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapperMain {

    static private String helpHeader = "Project 3: MapReduce and DFS. 15-440, Fall 2013.";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";


    public static void main(String[] args) {
        // read from the specified conf file, then try to connect to a thing.

        Options opt = new Options();
        opt.addOption("c", "conf", true, "Configuration file. Must be specified.");
        opt.addOption("h", "help", false, "Display help.");

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("server", helpHeader, opt, helpFooter, true);
                System.exit(1);
            }

            if (!cmd.hasOption("c")) {
                System.out.println("Please specify a configuration file.");
                System.exit(1);
            }

            System.out.println("Starting JobTracker...");

            File confLocation = new File(cmd.getOptionValue("c", "TrackerConf.ini"));

            MapperConf conf = new MapperConf.MapperConfBuilder().buildFromFile(confLocation);

            try {
                Socket sock = new Socket(conf.getAddress(), conf.getPort());
                MapNode map = new MapNode(sock, conf);
                map.run();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
