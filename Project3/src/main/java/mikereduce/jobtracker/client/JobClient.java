package mikereduce.jobtracker.client;

import mikereduce.jobtracker.server.ClientMessage;
import mikereduce.jobtracker.server.ClientMessageType;
import mikereduce.jobtracker.shared.ClientResponse;
import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.jobtracker.shared.JobState;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.Socket;

/**
 * Simple point of interaction with the MikeReduce framework. This submits
 * the job and receives updates until completion. Also allows users to list
 * running jobs as a management tool.
 */
public class JobClient {

    // No reason to construct an instance.
    private JobClient() {
    }

    static private String helpHeader = "Project 3: MapReduce and DFS. 15-440, Fall 2013.";
    static private String helpFooter =
            "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

    /**
     * Run the client CLI for the MapReduce framework. When pointed at a configuration file, parses the file
     * into a JobConfig and submits it to the framework for running. Then waits until the job is completed,
     * receiving periodic updates.
     * <p/>
     * The -l option will list running jobs instead of submitting a new one.
     *
     * @param args See help message.
     */
    public static void main(String[] args) {
        Options opt = new Options();
        opt.addOption("c", "conf", true, "Configuration file. Must be specified.");
        opt.addOption("l", "list", false, "Do not submit a job. Instead, list running ones.");
        opt.addOption("h", "help", false, "Display help.");
        opt.addOption("p", "port", false, "JobTracker port.");
        opt.addOption("a", "address", false, "JobTracker hostname.");

        try {
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse(opt, args);

            if (cmd.hasOption("l")) {
                System.out.println("-- Listing jobs --");

                ClientMessage cm = new ClientMessage(ClientMessageType.LIST);

                submit(cm, cmd.getOptionValue("a", "localhost"),
                        Integer.parseInt(cmd.getOptionValue("p", "9001")));
                return;
            }


            if (cmd.hasOption("h")) {
                HelpFormatter help = new HelpFormatter();
                help.printHelp("server", helpHeader, opt, helpFooter, true);
                System.exit(1);
            }

            if (!cmd.hasOption("c")) {
                System.out.println("Please specify a configuration file.");
                System.exit(1);
            }

            File confLocation = new File(cmd.getOptionValue("c", "ClientConf.ini"));
            JobConfig conf = new JobConfig(confLocation);

            ClientMessage cm = new ClientMessage(ClientMessageType.NEW, conf);

            submit(cm, cmd.getOptionValue("a", "localhost"),
                    Integer.parseInt(cmd.getOptionValue("p", "9001")));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a job to the framework in order to run it. Receive updates until completion.
     *
     * @param conf the JobConf of the job
     * @param addr address of the JobTracker
     * @param port port of the JobTracker
     */
    public static void submit(ClientMessage conf, String addr, int port) {
        Socket sock;

        ObjectInputStream ois;
        ObjectOutputStream oos;

        try {
            sock = new Socket(addr, port);

            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());

            // Send the config
            oos.writeObject(conf);
            oos.flush();

            // Read responses until the job is completed.
            while (!sock.isClosed()) {
                ClientResponse message;
                try {
                    message = (ClientResponse) ois.readObject();
                    System.out.println(message.getMessage());
                    if (message.getState() == JobState.COMPLETED) {
                        break;
                    }

                } catch (EOFException e) {
                    // There is no need to be upset.
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
