package messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import registry.RrefTracker;
import remote.Remote440;
import remote.Remote440Exception;
import remote.RemoteObjectRef;

/**
 */
public class RegistryMessageResponder implements Runnable {

    private Socket sock;
    private RegistryMessage message;

    public RegistryMessageResponder(Socket sock) {
        this.sock = sock;
    }

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

    public void run() {
        try {
            RrefTracker refs = RrefTracker.getInstance();
            this.receiveMessage();
            System.out.printf("Received request of type: %s\n",
                    message.getSubtype().toString());
            switch (message.getSubtype()) {
                case BIND: {
                    refs.bind(message.getName(), message.getRref());
                    sendReply(RegistryMessage.newAck());
                    break;
                }
                case REBIND: {
                    refs.rebind(message.getName(), message.getRref());
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
