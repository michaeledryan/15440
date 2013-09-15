package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import common.ClientRequest;
import common.ClientRequestType;
import common.Util;

/**
 * Main class that runs a client process. The client can be thought of as
 * essentially a testing harness for the assignment. It has two modes, reading a
 * trace file with -t or as an interactive repl.
 *
 * Requests are of the form: <delay in ms> <fully qualified class name of
 * process> <input args>
 *
 * @author acappiel
 *
 */
public class Main {

        static private String helpHeader = "Project 1: Process Migration. 15-440, Fall 2013.";
        static private String helpFooter = "Alex Cappiello (acappiel) and Michael Ryan (mer1).";
        static private String format = "Bad input. Try again. Format: [delay] <START | MIGRATE | LIST> <arguments>";

        private static String masterAddress;
        private static int masterPort;
        private static String prompt = "--> ";
        private static Random randGen;
        private static int basePid;
        private static int nextPid;
        private static AtomicInteger waitingCount;

        private static Socket sock = null;
        private static ObjectOutputStream outStream;

        private static ResponseManager responses;
        private static Thread t;

        /**
         * Startup tasks.
         *
         * @throws UnknownHostException
         * @throws IOException
         */
        private static void init() throws UnknownHostException, IOException {
                randGen = new Random();
                basePid = randGen.nextInt(10000) * 10000;
                nextPid = basePid;
                waitingCount = new AtomicInteger(0);
                sock = new Socket(masterAddress, masterPort);

                // Only create these once!
                outStream = new ObjectOutputStream(sock.getOutputStream());
                responses = new ResponseManager(sock, waitingCount);

                t = new Thread(responses);
                t.start();
        }

        /**
         * Cleanup tasks.
         *
         * @throws IOException
         * @throws InterruptedException
         */
        private static void cleanup() throws IOException, InterruptedException {
                System.out.println("Waiting for outstanding jobs to complete...");
                while (waitingCount.get() > 0) {
                        Thread.sleep(100);
                }
                // Kill the socket to interrupt the thread. Is there anything
                // nicer to do instead?
                sock.close();
                t.join();
        }

        /**
         * Sends a request to the master over existing socket.
         *
         * @param message
         *            Command string.
         */
        private static void sendRequest(String message, ClientRequestType type) {
                try {
                        int pid;
                        if (type == ClientRequestType.START) {
                                pid = nextPid++;
                        }
                        else {
                                pid = 0;
                        }
                        System.out.printf("Sending: pid: %d, command: %s\n", pid, message);

                        if (type == ClientRequestType.MIGRATE) {
                                pid = basePid + Integer.parseInt(message);
                        }

                        ClientRequest req = new ClientRequest(pid, message, type);

                        outStream.writeObject(req);
                        waitingCount.getAndIncrement();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        /**
         * Handles either a full request file or a line from the repl.
         *
         * @param req
         *            Newline-delimited sequence of commands.
         */
        private static void runtrace(String req) {
                String[] lines = req.split("\n");
                for (int i = 0; i < lines.length; i++) {

                        String[] line = lines[i].trim().split(" ", 3);

                        String cmd;
                        String args = "";

                        if (line[0].matches("\\d+(\\.\\d+)?")) {
                                // Contains a delay argument

                                if (line.length < 3
                                                && !(line.length > 1 && line[1]
                                                                .equalsIgnoreCase("killall"))) {
                                        System.err
                                                        .println("Bad input. Try again. Format: [delay] <START | MIGRATE | LIST> <arguments>");
                                        continue;
                                }
                                try {
                                        int wait = Integer.parseInt(line[0]);
                                        if (wait > 0) {
                                                Thread.sleep(wait);
                                        }
                                } catch (NumberFormatException e) {
                                        System.err.println(format);
                                } catch (InterruptedException e) {

                                }

                                cmd = line[1];

                                if (line.length == 3) {
                                        args = line[2];
                                }

                        } else {
                                line = lines[i].trim().split(" ", 2);

                                // No delay argument
                                if (line.length < 2
                                                && !(line.length > 0 && line[0]
                                                                .equalsIgnoreCase("killall"))) {
                                        System.err
                                                        .println("Bad input. Try again. Format: [delay] <START | MIGRATE | LIST> <arguments>");
                                        continue;
                                }

                                cmd = line[0];
                                if (line.length == 2) {
                                        args = line[1];
                                }
                        }

                        ClientRequestType type = null;

                        try {
                                type = ClientRequestType.valueOf(cmd.toUpperCase());
                        } catch (IllegalArgumentException e) {
                                System.err
                                                .println("Bad input. Try again. Format: [delay] <START | MIGRATE | LIST> <arguments>");
                                continue;
                        }

                        sendRequest(args, type);

                        if (type == ClientRequestType.KILLALL) {
                                System.out.println("Sending kill messages.");
                                System.out.println("Exiting...");
                                System.exit(0);
                        }

                }
        }

        /**
         * Start the client.
         *
         * @param args
         * @throws IOException
         * @throws UnknownHostException
         * @throws InterruptedException
         */
        public static void main(String[] args) throws UnknownHostException,
                        IOException, InterruptedException {

                Options opt = new Options();
                opt.addOption("t", "trace-file", true, "Trace file.");
                opt.addOption("a", "address", true,
                                "Address of master node (ip or url). Default: localhost.");
                opt.addOption("p", "port", true,
                                "Port to connect to master. Default: 8000.");
                opt.addOption("?", "help", false, "Display help.");

                CommandLineParser parser = new GnuParser();
                try {
                        CommandLine cmd = parser.parse(opt, args);
                        if (cmd.hasOption("?")) {
                                HelpFormatter help = new HelpFormatter();
                                help.printHelp("java -jar client.jar", helpHeader, opt,
                                                helpFooter, true);
                                System.exit(1);
                        }
                        System.out.println("Starting client...");
                        masterAddress = cmd.getOptionValue("a", "127.0.0.1");
                        try {
                                masterPort = Integer.parseInt(cmd.getOptionValue("p", "8000"));
                        } catch (NumberFormatException e) {
                                System.err.println("Invalid command line args.");
                                System.exit(1);
                        }
                        init();
                        if (cmd.hasOption("t")) {
                                System.out.println("Running trace file...");
                                prompt = "";
                                responses.setPrompt(prompt);
                                try {
                                        String in = Util.readFile(cmd.getOptionValue("t"));
                                        runtrace(in);
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        } else {
                                System.out.println("Entered interactive client mode:");
                                Scanner stdin = new Scanner(System.in);
                                responses.setPrompt(prompt);

                                System.out.print(prompt);

                                while (stdin.hasNextLine()) {
                                        String line = stdin.nextLine();
                                        runtrace(line);
                                        System.out.print(prompt);
                                }
                                stdin.close();
                                System.out.println("Exiting...");
                        }
                        cleanup();
                } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

        }

}
