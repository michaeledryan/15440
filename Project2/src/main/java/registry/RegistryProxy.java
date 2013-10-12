package registry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.RegistryMessage;
import messages.RegistryMessageType;
import remote.Remote440;
import remote.Remote440Exception;

public class RegistryProxy implements Registry {

	private String host;
	private int port;

	public RegistryProxy(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

    private Object sendReceive(RegistryMessage m) throws Remote440Exception {
        Object obj = null;
        Socket sock;
        try {
            sock = new Socket(host, port);
            ObjectOutputStream oos =
                    new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream ois =
                    new ObjectInputStream(sock.getInputStream());

            oos.writeObject(m);

            obj = ois.readObject();

            sock.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return obj;
    }

	@Override
	public void bind(String name, Remote440 obj) throws Remote440Exception {
		RegistryMessage m = RegistryMessage.newBind(name, obj,
				RegistryMessageType.BIND);
		sendReceive(m);
	}

	@Override
	public void rebind(String name, Remote440 obj) throws Remote440Exception {
		RegistryMessage m = RegistryMessage.newBind(name, obj,
				RegistryMessageType.REBIND);
		sendReceive(m);
	}

	@Override
	public void unbind(String name) throws Remote440Exception {
		RegistryMessage m = RegistryMessage.newUnBind(name);
		sendReceive(m);
	}

	@Override
	public String[] list() throws Remote440Exception {
		RegistryMessage m = RegistryMessage.newList();
		return (String[]) sendReceive(m);
	}

	@Override
	public Remote440 lookup(String key) throws Remote440Exception {
		RegistryMessage m = RegistryMessage.newLookup(key);
		return (Remote440) sendReceive(m);
	}

}
