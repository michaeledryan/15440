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

	private static String masterAddress;
	private static int masterPort;
	private static int id; // Currently ignored by master.
	private static String prompt = "--> ";
	private static Random randGen;
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
			int pid = randGen.nextInt();
			System.out.printf("Sending: pid: %d, command: %s\n", pid, message);
			
			String[] commandAndType = message.split(" ", 2);
			
			if (commandAndType.length != 2) {
				System.out.println("Bad format. Usage: <START | STOP | MIGRATE> <command args>");
				return;
			}
			ClientRequest req = new ClientRequest(id, pid, message, type);
			
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
			
			String[] line = lines[i].split(" ", 3);
			
			if (line[0].equalsIgnoreCase("start")) {
				try {
					int wait = Integer.parseInt(line[1]);
					if (wait > 0) {
						Thread.sleep(wait);
					}
					sendRequest(line[2], ClientRequestType.START);
				} catch (NumberFormatException e) {
					System.err
							.println("Bad input. Try again. Format: <delay (ms)> <command>");
				} catch (InterruptedException e) {

				}
				
			} else if (line[0].equalsIgnoreCase("stop")) {
				sendRequest(lines[i], ClientRequestType.STOP);
				
			} else if (line[0].equalsIgnoreCase("migrate")) {
				sendRequest(lines[i], ClientRequestType.MIGRATE);
			} else {
				System.out.println("Bad format. Usage: <START | STOP | MIGRATE> <command args>");
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
		opt.addOption("i", "identifier", true, "Client identifier.");
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
				id = Integer.parseInt(cmd.getOptionValue("i", "1"));
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
