package master;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	
	static private String helpHeader = 
			"Project 1: Process Migration. 15-440, Fall 2013.";
	static private String helpFooter = 
			"Alex Cappiello (acappiel) and Michael Ryan (mer1).";
	
	private static int port;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Options opt = new Options();
		opt.addOption("h", "host-file", true, "Host list file for worker nodes.");
		opt.addOption("p", "port", true, "Port to listen on. Default: 8000.");
		opt.addOption("?", "help", false, "Display help.");
		
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cmd = parser.parse(opt, args);
			if (cmd.hasOption("?")) {
				HelpFormatter help = new HelpFormatter();
				help.printHelp("java -jar master.jar", helpHeader,
						opt, helpFooter, true);
				System.exit(1);
			}
			try {
				port = Integer.parseInt(cmd.getOptionValue("p", "8000"));
			}
			catch (NumberFormatException e) {
				System.err.println("Invalid port number.");
				System.exit(1);
			}
			Listener L = new Listener(port);
			L.run();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
