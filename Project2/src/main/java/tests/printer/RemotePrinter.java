package tests.printer;

import remote.Remote440;
import remote.Remote440Exception;

/**
 * Very simple remote object. Spits back the parameter passed to it.
 * 
 * @author Alex Cappiello and Michael Ryan
 * 
 */
public interface RemotePrinter extends Remote440 {

	public String printMessage(String message) throws Remote440Exception;

}
