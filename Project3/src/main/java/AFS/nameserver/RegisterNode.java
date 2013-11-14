package AFS.nameserver;

import AFS.message.StartupMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Adds a new data node to the map during startup.
 */
public class RegisterNode implements Runnable {

    private Socket s;

    public RegisterNode(Socket s) {
        this.s = s;
    }

    /**
     * The message from the data node contains its identity as well as all
     * files that it already holds.
     */
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            Object obj = in.readObject();
            if (!(obj instanceof StartupMessage)) {
                throw new IOException("Bad message.");
            }
            StartupMessage msg = (StartupMessage) obj;
            String host = msg.getHostname() + ":" + msg.getPort();
            String id = msg.getId();
            FileMap fmap = FileMap.getInstance();
            fmap.addNode(id, host);
            fmap.batchPut(msg.getFiles(), host);
            s.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
