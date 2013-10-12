package messages;

import registry.RrefTracker;
import remote.Remote440Exception;
import remote.RemoteObjectRef;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles an incoming message to the registry, then sends the response.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class RegistryMessageResponder implements Runnable {

    private Socket sock;
    private RegistryMessage message;

    public RegistryMessageResponder(Socket sock) {
        this.sock = sock;
    }

    /**
     * Receive a message from the socket and ensure it is the correct class.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void receiveMessage() throws IOException, ClassNotFoundException {
        ObjectInputStream inStream = new ObjectInputStream(this.sock
                .getInputStream());
        Object obj = inStream.readObject();
        if (!(obj instanceof RegistryMessage)) {
            throw new IOException("Received object that is not a " +
                    "RegistryMessage.");
        }
        this.message = (RegistryMessage) obj;
    }

    /**
     * Send a new message as the response and close the socket.
     *
     * @param ref REPLY message.
     */
    private void sendReply(RegistryMessage ref) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(this.sock
                    .getOutputStream());
            outStream.writeObject(ref);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Receive a message, process it, and send a reply. Each type of message
     * is handled differently.
     */
    public void run() {
        try {
            RrefTracker refs = RrefTracker.getInstance();
            this.receiveMessage();
            System.out.printf("Received request of type: %s\n",
                    message.getSubtype().toString());

            switch (message.getSubtype()) {
                case BIND: {
                    refs.bind(message.getName(), message.getRref(), null);
                    sendReply(RegistryMessage.newAck());
                    break;
                }
                case REBIND: {
                    refs.rebind(message.getName(), message.getRref(), null);
                    sendReply(RegistryMessage.newAck());
                    break;
                }
                case UNBIND: {
                    refs.unbind(message.getName());
                    sendReply(RegistryMessage.newAck());
                    break;
                }
                case LOOKUP: {
                    RemoteObjectRef rref = refs.lookup(message.getName());
                    sendReply(RegistryMessage.newReply(rref));
                    break;
                }
                case LIST: {
                    String[] data = refs.list();
                    sendReply(RegistryMessage.sendList(data));
                    break;
                }
            }
            System.out.println("Request complete.");
        } catch (Remote440Exception e) {
            sendReply(RegistryMessage.newExn(e));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
