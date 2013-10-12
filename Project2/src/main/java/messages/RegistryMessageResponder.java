package messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import registry.RrefTracker;
import remote.Remote440;

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
            switch (message.getSubtype()) {
                case BIND: {
                    refs.bind(message.getName(), message.getRref());
                    sendReply(RegistryMessage.newAck());
                }
                case REBIND: {
                    refs.rebind(message.getName(), message.getRref());
                    sendReply(RegistryMessage.newAck());
                }
                case UNBIND: {
                    refs.unbind(message.getName());
                    sendReply(RegistryMessage.newAck());
                }
                case LOOKUP: {
                    Remote440 rref = refs.lookup(message.getName());
                    sendReply(RegistryMessage.newReply(rref));
                }
                case LIST: {
                    String[] data = refs.list();
                    sendReply(RegistryMessage.sendList(data));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
