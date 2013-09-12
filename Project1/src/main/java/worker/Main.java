package worker;

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Options opt= new Options();
		opt.addOption("h", "host-file", true, "Host list file for worker nodes.");
		opt.addOption("t", "trace-file", true, "Trace file.");
		opt.addOption("?", "help", false, "Display help.");
		
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cmd = parser.parse(opt, args);
			if (cmd.hasOption("?")) {
				HelpFormatter help = new HelpFormatter();
				help.printHelp("worker", helpHeader,
						opt, helpFooter, true);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
