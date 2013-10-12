package tests.ints;

import remote.Remote440Exception;

/**
 * Implementation of a RemoteIntger for the server side. Fairly straightforward.
 * 
 * @author Michael Ryan and Alex Cappiello
 * 
 */
public class RemoteIntegerImpl implements RemoteInteger {

	private int actualInt;

	public RemoteIntegerImpl(int actual) {
		this.actualInt = actual;
	}

	@Override
	public void destructiveAdd(RemoteInteger addend) throws Remote440Exception {
		actualInt += addend.getValue();
	}

	@Override
	public void destructiveSum(RemoteInteger[] addends)
			throws Remote440Exception {
		for (RemoteInteger ri : addends) {
			this.actualInt += ri.getValue();
		}
	}

	@Override
	public Integer getValue() throws Remote440Exception {
		return actualInt;
	}

}
