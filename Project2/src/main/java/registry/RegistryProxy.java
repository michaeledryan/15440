package registry;

import messages.RegistryMessage;
import messages.RegistryMessageType;
import remote.Remote440;
import remote.Remote440Exception;
import remote.RemoteObjectRef;
import server.ObjectTracker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Implements the Registry interface, complying with the descriptions of each
 * function in the interface.
 * Client/Server side access to the registry.
 * It's essentially a stub to RrefTracker, so RMI-ception.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class RegistryProxy implements Registry {

    private String host;
    private int port;

    public RegistryProxy(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    /**
     * Send a request off to the actual registry and wait for a reply.
     *
     * @param m A REQUEST message.
     * @return A REPLY message.
     * @throws Remote440Exception
     */
    private RegistryMessage sendReceive(RegistryMessage m) throws Remote440Exception {
        Object obj = null;
        Socket sock;
        try {
            sock = new Socket(host, port);
            ObjectOutputStream oos =
                    new ObjectOutputStream(sock.getOutputStream());

            oos.writeObject(m);

            ObjectInputStream ois =
                    new ObjectInputStream(sock.getInputStream());

            obj = ois.readObject();

            sock.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        RegistryMessage res = (RegistryMessage) obj;

        if (res == null) {
            throw new Remote440Exception("Message is null.");
        } else if (res.getSubtype() == RegistryMessageType.EXN) {
            throw res.getExn();
        }

        return (RegistryMessage) obj;
    }

    @Override
    public void bind(String name, RemoteObjectRef ref, Remote440 obj) throws Remote440Exception {
        RegistryMessage m = RegistryMessage.newBind(name, ref,
                RegistryMessageType.BIND);
        ObjectTracker.getInstance().put(name, obj);
        sendReceive(m);
    }

    @Override
    public void rebind(String name, RemoteObjectRef ref, Remote440 obj) throws Remote440Exception {
        RegistryMessage m = RegistryMessage.newBind(name, ref,
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
        return sendReceive(m).getList();
    }

    @Override
    public Remote440 lookup(String key) throws Remote440Exception {
        RegistryMessage m = RegistryMessage.newLookup(key);

        RemoteObjectRef ror = sendReceive(m).getRref();
        if (ror == null) {
            return null;
        }
        try {
            return (Remote440) ror.localize();
        } catch (ClassNotFoundException e) {
            throw new Remote440Exception("Class not found.", e);
        }
    }

}
