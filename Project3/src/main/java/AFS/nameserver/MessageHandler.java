package AFS.nameserver;

import AFS.message.Message;
import AFS.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 */
public class MessageHandler implements Runnable {

    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MessageHandler(Socket s) {
        System.out.println("foo");
        this.s = s;
        System.out.println("bar");
    }

    private Message readMessage() throws IOException {
        Object obj = null;
        try {
            obj = in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (obj == null || !(obj instanceof Message)) {
            throw new IOException("Failed to get message.");
        }
        return (Message)obj;
    }

    public void run() {
        try {
            this.in = new ObjectInputStream(s.getInputStream());
            this.out = new ObjectOutputStream(s.getOutputStream());
            while (s.isConnected()) {
                Message m = readMessage();
                Message resp = Message.ack();
                FileMap fmap = FileMap.getInstance();
                String path;
                String host;

                // TODO: DELETE.
                // TODO: Add failure messages.
                switch (m.getType()) {
                    case LOCATION:
                        path = m.getPath();
                        host = fmap.get(path);
                        if (host == null) {
                            host = "";
                        }
                        resp = Message.location(host);
                        break;
                    case WRITE:
                        path = m.getPath();
                        if (fmap.contains(path)) {
                            host = fmap.get(path);
                        } else {
                            host = fmap.randomHost();
                        }
                        resp = Message.location(host);
                        break;
                    case CREATE:
                        path = m.getPath();
                        host = m.getData();
                        FileMap.getInstance().put(path, host);
                        break;
                    default:
                        break;
                }

                out.writeObject(resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
