package AFS;

import AFS.message.Message;
import AFS.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * It is NOT thread-safe to have multiple concurrent read requests on a single
 * Connection object.
 */
public class Connection {

    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Connection(String host, int port) {
        try {
            s = new Socket(host, port);
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLocation(String path) throws IOException {
        Message req = Message.read(path);
        out.writeObject(req);
        Object obj = null;
        try {
            obj = in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (obj == null || !(obj instanceof Message)) {
            throw new IOException("Failed to get message.");
        }
        Message loc = (Message)obj;
        if (loc.getType() != MessageType.LOCATION) {
            throw new IOException("Bad message type.");
        }
        return loc.getPath();
    }

    public String readFile(String path) throws IOException {
        String loc = this.getLocation(path);
        return "";
    }

    public String readBlock(String path, int start, int size) {
        return "";
    }

    public void writeFile(String path, String data) {

    }

    public void deleteFile(String path) {

    }

}
