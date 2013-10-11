package registry;

import java.rmi.RemoteException;
import java.util.Set;

import remote.MyRemote;

public interface Registry {

	public void bind(String name, MyRemote obj) throws RemoteException;

	public void rebind(String name, MyRemote obj) throws RemoteException;

	public void unbind(String name) throws RemoteException;

	public Set<String> list() throws RemoteException;
}
