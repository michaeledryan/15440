package tests.printer;

import remote.Remote440Exception;

/**
 * Actual class for the extremely simple RemotePrinter. Acknowledges the string
 * sent along as a parameter and appends a short suffix.
 * 
 * @author michaelryan
 * 
 */
public class RemotePrinterImpl implements RemotePrinter {

	public RemotePrinterImpl() {
		super();
	}

	@Override
	public String printMessage(String message) throws Remote440Exception {
		return "YOUR MESSAGE: " + message;
	}

}
