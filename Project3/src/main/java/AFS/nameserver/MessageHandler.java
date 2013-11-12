package AFS.nameserver;

import AFS.message.Message;

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
        this.s = s;
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
        return (Message) obj;
    }

    public void run() {
        try {
            this.in = new ObjectInputStream(s.getInputStream());
            this.out = new ObjectOutputStream(s.getOutputStream());
            while (s.isConnected()) {
                Message m = readMessage();
                Message resp = Message.ack();
                FileMap fmap = FileMap.getInstance();
                String path = m.getPath();
                String host;

                switch (m.getType()) {
                    case LOCATION:
                        if (!fmap.contains(path)) {
                            resp = Message.error(
                                    new IOException("Unknown file."));
                        } else {
                            host = fmap.get(path);
                            resp = Message.location(host);
                        }
                        break;
                    case WRITE:
                        if (fmap.contains(path)) {
                            host = fmap.get(path);
                        } else {
                            host = fmap.randomHost();
                        }
                        resp = Message.location(host);
                        break;
                    case CREATE:
                        host = m.getData();
                        if (fmap.contains(path)) {
                            resp = Message.error(
                                    new IOException("File already exists."));
                        } else if (fmap.validHost(host)) {
                            fmap.put(path, host);
                        } else {
                            resp = Message.error(
                                    new IOException("Unknown data node."));
                        }
                        break;
                    case DELETE:
                        if (fmap.contains(path)) {
                            host = fmap.get(path);
                            fmap.delete(path);
                            resp = Message.location(host);
                        } else {
                            resp = Message.error(
                                    new IOException("Unknown file."));
                        }
                    default:
                        break;
                }

                out.writeObject(resp);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }

}
