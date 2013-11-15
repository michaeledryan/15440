package AFS.nameserver;

import AFS.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Responds to messages from a client.
 */
public class MessageHandler implements Runnable {

    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MessageHandler(Socket s) {
        this.s = s;
    }

    /**
     * Reads a message from the socket.
     *
     * @return Message received.
     * @throws IOException
     */
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

    /**
     * Serves this client until it hangs up.
     */
    public void run() {
        try {
            this.in = new ObjectInputStream(s.getInputStream());
            this.out = new ObjectOutputStream(s.getOutputStream());
            while (s.isConnected()) {
                Message m = readMessage();
                // Default reply. Overwritten where necessary.
                Message resp = Message.ack();
                FileMap fmap = FileMap.getInstance();
                String path = m.getPath();
                String host;
                String priority;

                switch (m.getType()) {
                    // Send the data node that contains the file.
                    case LOCATION:
                        if (!fmap.contains(path)) {
                            resp = Message.error(
                                    new IOException("Unknown file."));
                        } else {
                            priority = m.getData();
                            if (priority != null) {
                                host = fmap.priorityGet(path, priority);
                            } else {
                                host = fmap.get(path);
                            }
                            resp = Message.location(host);
                        }
                        break;
                    // If the file does not exist, assign to a random data
                    // node, then send back the location.
                    case WRITE:
                        if (fmap.contains(path)) {
                            host = fmap.get(path);
                        } else {
                            ArrayList<String> tmp = fmap.randomHosts(fmap
                                    .getReplication());
                            fmap.putAll(path, tmp);
                            host = fmap.flattenHosts(tmp);
                        }
                        resp = Message.location(host);
                        break;
                    // Creates a record for the file on the given data node.
                    case CREATE:
                        String id = m.getData();
                        if (fmap.contains(path)) {
                            resp = Message.error(
                                    new IOException("File already exists."));
                        } else if (fmap.validHost(id)) {
                            fmap.priorityPut(path, id);
                        } else {
                            resp = Message.error(
                                    new IOException("Unknown data node."));
                        }
                        break;
                    // Remove record from the index.
                    case DELETE:
                        if (fmap.contains(path)) {
                            host = fmap.get(path);
                            fmap.delete(path);
                            resp = Message.location(host);
                        } else {
                            resp = Message.error(
                                    new IOException("Unknown file."));
                        }
                    // Should not exist. Ignore them.
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
