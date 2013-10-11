package registry;

import java.rmi.RemoteException;
import java.util.List;

import remote.Remote440;

public interface Registry {

	public void bind(String name, Remote440 obj) throws RemoteException;

	public void rebind(String name, Remote440 obj) throws RemoteException;

	public void unbind(String name) throws RemoteException;

	public List<String> list() throws RemoteException;
}
