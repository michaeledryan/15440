package AFS.nameserver;

import AFS.message.StartupMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 */
public class RegisterNode implements Runnable {

    private Socket s;

    public RegisterNode(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            Object obj = in.readObject();
            if (!(obj instanceof StartupMessage)) {
                throw new IOException("Bad message.");
            }
            StartupMessage msg = (StartupMessage)obj;
            String id = msg.getHostname() + ":" + msg.getPort();
            FileMap fmap = FileMap.getInstance();
            fmap.addNode(id);
            fmap.batchPut(msg.getFiles(), id);
            s.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
