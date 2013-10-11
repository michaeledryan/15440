package messages;

import registry.RrefTracker;
import remote.MyRemote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 */
public class RegistryMessageResponder implements Runnable {

    private Socket sock;
    private RrefTracker refs;
    private RegistryMessage message;

    public RegistryMessageResponder(Socket sock, RrefTracker refs) {
        this.sock = sock;
        this.refs = refs;
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
            this.receiveMessage();
            MyRemote ref = this.refs.lookup(this.message.getName());
            RegistryMessage resp = RegistryMessage.newReply(ref);
            this.sendReply(resp);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
