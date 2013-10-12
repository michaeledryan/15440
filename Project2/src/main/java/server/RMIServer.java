package server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import registry.Registry;
import registry.RegistryProxy;

public class RMIServer {
	static private String helpHeader = "Project 2: RMI. 15-440, Fall 2013.";
	static private String helpFooter = "Alex Cappiello (acappiel) and Michael Ryan (mer1).";

	private int port;

	private int rport = 8000;
	private String registryHost = "";
	private Registry registry;
	
	public int getPort() {
		return port;
	}
	
	public Registry getRegistry() {
		return registry;
	}
	
	public void startServer(String[] args) {
		Options opt = new Options();
		opt.addOption("p", "port", true, "Port to listen on. Default: 1099.");
		opt.addOption("rp", "registry port", true, "Port to look for the registry on. Default: 8000.");
		opt.addOption("r", "registry host", true, "Host to look for the registry on. Default: localhost.");
		opt.addOption("h", "help", false, "Display help.");

		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cmd = parser.parse(opt, args);
			if (cmd.hasOption("h")) {
				HelpFormatter help = new HelpFormatter();
				help.printHelp("server", helpHeader, opt, helpFooter, true);
				System.exit(1);
			}
			System.out.println("Starting server...");
			String portString = cmd.getOptionValue("p", "1099");
			String rportString = cmd.getOptionValue("rp", "8000");
			registryHost = cmd.getOptionValue("r", "localhost");
			try {
				port = Integer.parseInt(portString);
				rport = Integer.parseInt(rportString);
			} catch (NumberFormatException e) {
				System.err.printf("Invalid port number: %s\n", portString);
				System.exit(1);
			}
			Listener l = new Listener(port);
			registry = new RegistryProxy(registryHost, rport);
			new Thread(l).start();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}
