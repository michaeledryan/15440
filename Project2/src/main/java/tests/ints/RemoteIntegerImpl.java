package tests.ints;

import remote.Remote440Exception;

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
		System.out.println("DOING SUM...");
		System.out.println(addends.length);
		for(RemoteInteger ri : addends) {
			this.actualInt+= ri.getValue();
		}
	}

	@Override
	public Integer getValue() throws Remote440Exception {
		return actualInt;
	}

}
