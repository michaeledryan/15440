package registry;

import java.rmi.RemoteException;
import java.util.List;

import remote.MyRemote;

public interface Registry {

	public void bind(String name, MyRemote obj) throws RemoteException;

	public void rebind(String name, MyRemote obj) throws RemoteException;

	public void unbind(String name) throws RemoteException;

	public List<String> list() throws RemoteException;
}
