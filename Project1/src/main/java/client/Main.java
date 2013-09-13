package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import common.ClientRequest;
import common.Util;

public class Main {

	static private String helpHeader = "Project 1: Process Migration. 15-440, Fall 2013.";
	static private String helpFooter = "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

	private static String masterAddress;
	private static int masterPort;
	private static int id;
	private static String prompt = "--> ";

	private static Socket sock = null;

	private static ResponseManager responses;
	private static Thread t;

	private static void init() throws UnknownHostException, IOException {
		sock = new Socket(masterAddress, masterPort);
		responses = new ResponseManager(sock);
		responses.setPrompt(prompt);
		t = new Thread(responses);
		t.start();
	}

	private static void cleanup() throws IOException, InterruptedException {
		sock.close();
		t.join();
	}

	private static void sendRequest(String message) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					sock.getOutputStream());
			ClientRequest req = new ClientRequest(id, message);
			out.writeObject(req);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void runtrace(String req) {
		String[] lines = req.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String[] line = lines[i].split(" ", 2);
			if (line[0].compareTo("") == 0) {
				return;
			} else if (line.length != 2) {
				System.err
						.println("Bad input. Try again. Format: <delay (ms)> <command>");
				return;
			}
			try {
				int wait = Integer.parseInt(line[0]);
				if (wait > 0) {
					Thread.sleep(wait);
				}
				System.out.printf("Sending: %s\n", line[1]);
				sendRequest(line[1]);
			} catch (NumberFormatException e) {
				System.err
						.println("Bad input. Try again. Format: <delay (ms)> <command>");
			} catch (InterruptedException e) {

			}
		}
	}

	/**
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
				try {
					String in = Util.readFile(cmd.getOptionValue("t"));
					runtrace(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Entered interactive client mode:");
				Scanner stdin = new Scanner(System.in);

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