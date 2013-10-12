package tests.printer;

import remote.Remote440;
import remote.Remote440Exception;

public interface RemotePrinter extends Remote440 {

	public String printMessage(String message) throws Remote440Exception;

}
