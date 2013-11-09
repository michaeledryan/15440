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

    private static Message readReply(ObjectInputStream in) throws IOException {
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

    private String getLocation(Message req) throws IOException {
        if (req.getType() != MessageType.LOCATION) {
            throw new IOException("Bad request type.");
        }
        out.writeObject(req);
        Message loc = readReply(this.in);
        if (loc.getType() != MessageType.LOCATION) {
            throw new IOException("Bad message type.");
        }
        return loc.getPath();
    }

    private static Socket connectToDataNode(String hoststring)
            throws IOException {
        String[] loc = hoststring.split(":");
        if (loc.length != 2) {
            throw new IOException("Bad host reply.");
        }
        int port = 0;
        try {
            port = Integer.parseInt(loc[1]);
        } catch (NumberFormatException e) {
            throw new IOException("Bad port.");
        }
        return new Socket(loc[0], port);
    }

    public String readFile(String path) throws IOException {
        Message getloc = Message.location(path);
        String loc = this.getLocation(getloc);

        Socket node = connectToDataNode(loc);
        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());

        Message req = Message.read(path);
        nodeOut.writeObject(req);
        Message rep = readReply(nodeIn);
        if (rep.getType() != MessageType.DATA) {
            throw new IOException("Bad message type.");
        }
        return rep.getData();
    }

    public String readBlock(String path, int start, int size)
            throws IOException{
        Message getloc = Message.location(path);
        String loc = this.getLocation(getloc);

        Socket node = connectToDataNode(loc);
        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());

        Message req = Message.readBlock(path, start, size);
        nodeOut.writeObject(req);
        Message rep = readReply(nodeIn);
        if (rep.getType() != MessageType.DATA) {
            throw new IOException("Bad message type.");
        }
        return rep.getData();
    }

    public void writeFile(String path, String data) throws IOException {
        Message getloc = Message.location(path);
        String loc = this.getLocation(getloc);

        Socket node = connectToDataNode(loc);
        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());

        Message req = Message.write(path, data);
        nodeOut.writeObject(req);
        Message rep = readReply(nodeIn);
        if (rep.getType() != MessageType.ACK) {
            throw new IOException("Bad message type.");
        }
    }

    public void deleteFile(String path) throws IOException {
        Message getloc = Message.location(path);
        String loc = this.getLocation(getloc);

        Socket node = connectToDataNode(loc);
        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());

        Message req = Message.delete(path);
        nodeOut.writeObject(req);
        Message rep = readReply(nodeIn);
        if (rep.getType() != MessageType.ACK) {
            throw new IOException("Bad message type.");
        }
    }

}
