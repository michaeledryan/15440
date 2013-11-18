package AFS.management;

import AFS.message.Message;
import AFS.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 */
public class ToolImpl {

    private Socket s;

    public ToolImpl(String host, int port) throws IOException {
        this.s = new Socket(host, port);
    }

    /**
     * Wait for a message to arrive and ensure it is a valid Message.
     *
     * @return Received message.
     * @throws IOException
     */
    private Message readReply() throws IOException {
        Object obj = null;
        try {
            ObjectInputStream in =
                    new ObjectInputStream(s.getInputStream());
            obj = in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (obj == null || !(obj instanceof Message)) {
            throw new IOException("Failed to get message.");
        }
        return (Message) obj;
    }

    private String readRequest(Message req) throws Exception {

        ObjectOutputStream out =
                new ObjectOutputStream(s.getOutputStream());
        out.writeObject(req);

        Message rep = readReply();
        s.close();

        if (rep.getType() == MessageType.ERROR) {
            throw rep.getException();
        } else if (rep.getType() != MessageType.DATA) {
            throw new IOException("Bad message type.");
        }
        return rep.getData();

    }

    public void query(String type, String arg) {
        try {
            Message q = Message.adminQuery(type, arg);
            System.out.println(readRequest(q));
        } catch (IllegalArgumentException e) {
            System.err.println("Bad task: " + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
