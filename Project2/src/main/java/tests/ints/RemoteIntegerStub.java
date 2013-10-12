package tests.ints;

import java.io.IOException;

import remote.Marshal;
import remote.Remote440Exception;
import remote.RemoteObjectRef;
import remote.RemoteStub;

/**
 * Stub class for RemoteInteger. Handwritten. Each stub method prepares a
 * message to be sent, Marshals it, and waits for a return value.
 * 
 * @author michaelryan
 * 
 */
public class RemoteIntegerStub implements RemoteInteger, RemoteStub {

	private static final long serialVersionUID = -8372536048460270233L;
	private RemoteObjectRef remoteRef;

	public RemoteIntegerStub() {
		super();
	}

	@Override
	public void setRemoteRef(RemoteObjectRef ror) {
		this.remoteRef = ror;
	}

	@Override
	public void destructiveAdd(RemoteInteger addend) throws Remote440Exception {
		if (remoteRef == null) {
			throw new Remote440Exception("No RemoteObjectRef specified");
		}

		Marshal mars = new Marshal(remoteRef);

		Class<?>[] clazzes = { RemoteInteger.class };

		Object[] objs = { addend };

		try {
			mars.run("destructiveAdd", objs, clazzes);
		} catch (IOException e) {
			throw new Remote440Exception(e);
		}

		return;

	}

	@Override
	public void destructiveSum(RemoteInteger[] addends)
			throws Remote440Exception {

		if (remoteRef == null) {
			throw new Remote440Exception("No RemoteObjectRef specified");
		}

		Marshal mars = new Marshal(remoteRef);

		Class<?>[] clazzes = { RemoteInteger[].class };

		Object[] args = { addends };

		try {
			mars.run("destructiveSum", args, clazzes);
		} catch (IOException e) {
			throw new Remote440Exception(e);
		}

		return;
	}

	@Override
	public Integer getValue() throws Remote440Exception {
		if (remoteRef == null) {
			throw new Remote440Exception("No RemoteObjectRef specified");
		}

		Marshal mars = new Marshal(remoteRef);

		Class<?>[] clazzes = {};

		Object[] objs = {};

		Object res = null;
		try {
			res = mars.run("getValue", objs, clazzes);
		} catch (IOException e) {
			throw new Remote440Exception(e);
		}

		return (Integer) res;

	}

}
