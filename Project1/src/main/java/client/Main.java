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
	
	static private String helpHeader = 
			"Project 1: Process Migration. 15-440, Fall 2013.";
	static private String helpFooter = 
			"Alex Cappiello (acappiel) and Michael Ryan (mer1).";
	
	private static String masterAddress;
	private static int masterPort;
	
	private static void sendRequest(String message) {
		Socket sock = null;
		try {
			sock = new Socket(masterAddress, masterPort);
			ObjectOutputStream out = new ObjectOutputStream(
					sock.getOutputStream());
			out.writeObject(new ClientRequest(message));
			sock.close();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
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
				System.err.println("Bad input. Try again. Format: <delay (ms)> <command>");
				return;
			}
			try {
				int wait = Integer.parseInt(line[0]);
				if (wait > 0) {
					Thread.sleep(wait);
				}
				System.out.println(line[1]);
				sendRequest(line[1]);
			}
			catch (NumberFormatException e) {
				System.err.println("Bad input. Try again. Format: <delay (ms)> <command>");
			}
			catch (InterruptedException e) {
				
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Options opt= new Options();
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
				help.printHelp("java -jar client.jar", helpHeader,
						opt, helpFooter, true);
				System.exit(1);
			}
			masterAddress = cmd.getOptionValue("a", "127.0.0.1");
			try {
				masterPort = Integer.parseInt(cmd.getOptionValue("p", "8000"));
			}
			catch (NumberFormatException e) {
				System.err.println("Invalid port number.");
				System.exit(1);
			}
			System.out.println(masterAddress);
			if (cmd.hasOption("t")) {
				System.out.println("Running trace file...");
				try {
					String in = Util.readFile(cmd.getOptionValue("t"));
					runtrace(in);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Entered interactive client mode:");
				Scanner stdin = new Scanner(System.in);
				
				System.out.print("--> ");
				while (stdin.hasNextLine()) {
					String line = stdin.nextLine();
					runtrace(line);
					System.out.print("--> ");
				}
				stdin.close();
				System.out.println("Exiting...");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}