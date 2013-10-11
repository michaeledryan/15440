package toys;

import java.io.IOException;
import java.rmi.RemoteException;

import util.RemoteObjectRef;
import util.RemoteStub;
import client.Marshal;

public class ToyClassImplStub implements ToyClass, RemoteStub{

	private RemoteObjectRef remoteRef;
	
	public ToyClassImplStub() {
		super();
	}
	
	@Override
	public String printMessage(String string) throws Remote440Exception {
		if (remoteRef == null) {
			throw new Remote440Exception("No RemoteObjectRef specified");
		}
		
		Marshal mars = new Marshal(remoteRef);
		
		Class<?>[] clazzes = { String.class };

		Object[] objs = { string };
		
		Object res = null;
		try {
			res = mars.run("printMessage", objs, clazzes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (String) res;

	}


	public RemoteObjectRef getRemoteRef() {
		return remoteRef;
	}

	@Override
	public void setRemoteRef(RemoteObjectRef ror) {
		this.remoteRef = ror;
	}	
	
}