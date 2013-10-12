package registry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Set;

import messages.RegistryMessage;
import messages.RegistryMessageType;
import remote.Remote440;

public class RegistryProxy implements Registry {

	private String host;
	private int port;

	public RegistryProxy(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	@Override
	public void bind(String name, Remote440 obj) throws RemoteException {
		RegistryMessage m = RegistryMessage.newBind(name, obj,
				RegistryMessageType.BIND);
		Socket sock;
		try {
			sock = new Socket(host, port);
			ObjectOutputStream oos = new ObjectOutputStream(
					sock.getOutputStream());

			oos.writeObject(m);
			
			
			sock.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void rebind(String name, Remote440 obj) throws RemoteException {
		RegistryMessage m = RegistryMessage.newBind(name, obj,
				RegistryMessageType.REBIND);
		Socket sock;
		try {
			sock = new Socket(host, port);
			ObjectOutputStream oos = new ObjectOutputStream(
					sock.getOutputStream());

			oos.writeObject(m);
			
			sock.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void unbind(String name) throws RemoteException {
		RegistryMessage m = RegistryMessage.newUnBind(name);
		Socket sock;
		try {
			sock = new Socket(host, port);
			ObjectOutputStream oos = new ObjectOutputStream(
					sock.getOutputStream());

			oos.writeObject(m);
			
			sock.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> list() throws RemoteException {
		RegistryMessage m = RegistryMessage.newList();
		Socket sock;
		try {
			sock = new Socket(host, port);
			ObjectOutputStream oos = new ObjectOutputStream(
					sock.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());

			oos.writeObject(m);

			Object obj = ois.readObject();
			
			sock.close();
			return (Set<String>) obj;

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
