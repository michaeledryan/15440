package mikereduce.jobtracker.server;

import org.apache.commons.cli.*;

import java.io.File;

/**
 * Central node for the MapReduce framework. Receives requests for jobs, then
 * dispatches map and reduce tasks to appropriate nodes.
 */
public class JobTracker {


    static private String helpHeader = "Project 3: MapReduce and DFS. 15-440, Fall 2013.";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    private JobTrackerConf conf;
    private static JobTracker instance;


    public JobTracker getInstance() {
        return instance;
    }

   private JobTracker(JobTrackerConf conf) {
       this.conf = conf;
   }

    public static void main(String[] args) {
        Options opt = new Options();
        opt.addOption("c", "conf", true, "Configuration file. Must be specified.");
        opt.addOption("h", "help", false, "Display help.");

        try {
            CommandLineParser parser = new GnuParser();
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

            File confLocation = new File(cmd.getOptionValue("c", "TrackerConf.ini"));
            JobTrackerConf conf = new JobTrackerConf(confLocation);

            instance = new JobTracker(conf);

            System.out.println("Starting JobTracker...");

            ClientListener client = ClientListener.setupClientListener(conf.getClientPort());

            WorkerListener worker;
            WorkerListener.setPort(conf.getWorkerPort());
            worker = WorkerListener.getInstance();

            new Thread(client).start();
            new Thread(worker).start();


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    // Need a way to share the location of each worker node.

}