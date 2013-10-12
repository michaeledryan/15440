package tests.ints;

import remote.Remote440;
import remote.Remote440Exception;

/**
 * Simple test class. Integers that are accessible by remote reference. Only
 * supports addition, but this is purely a vehicle for displaying framework
 * functionality. Complicated operations take a backseat to clarity.
 * 
 * @author Michael Ryan and Alex Cappiello
 * 
 */
public interface RemoteInteger extends Remote440 {

	/**
	 * Destructive addition. Modifies this instance of RemoteInteger.
	 * 
	 * @param addend
	 *            number to be added to this RemoteInteger
	 * @throws Remote440Exception
	 */
	public void destructiveAdd(RemoteInteger addend) throws Remote440Exception;

	/**
	 * Destructive summation - returns a new RemoteInteger.
	 * 
	 * @param addends
	 *            numbers to be added to this RemoteInteger to create a new one
	 * @return a new RemoteInteger, the sum of this and all addends.
	 * @throws Remote440Exception
	 */
	public void destructiveSum(RemoteInteger[] addends)
			throws Remote440Exception;

	/**
	 * @return the value stored in this RemoteInteger
	 * @throws Remote440Exception
	 */
	public Integer getValue() throws Remote440Exception;

}
