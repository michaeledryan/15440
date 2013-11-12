package AFS;

import AFS.message.Message;
import AFS.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// TODO: Add failure messages.

/**
 * Establish a connection with the nameserver to have access to the files
 * distributed across the nodes.
 *
 * It is NOT thread-safe to have multiple concurrent read requests on a single
 * Connection object.
 */
public class Connection {

    private Socket s;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    /**
     * Connects to the nameserver.
     * @param host Nameserver hostname.
     * @param port Nameserver port.
     */
    public Connection(String host, int port) {
        try {
            s = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wait for a message to arrive and ensure it is a valid Message.
     * @param in ObjectInputStream to read from.
     * @return Received message.
     * @throws IOException
     */
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

    /**
     * Reads a reply from the nameserver, opening the InputStream,
     * if necessary.
     * @return Received message.
     * @throws IOException
     */
    private Message read() throws IOException {
        if (in == null) {
            in = new ObjectInputStream(s.getInputStream());
        }
        return readReply(in);
    }

    /**
     * Sends a message to the namesever, opening the ObjectOutputStream if
     * necessary.
     * @param m Message to send.
     * @throws IOException
     */
    private void write(Message m) throws IOException {
        if (out == null) {
            out = new ObjectOutputStream(s.getOutputStream());
        }
        out.writeObject(m);
    }

    /**
     * Determines the datanode that a particular file is located on.
     * @param req Message containing the path to look up.
     * @return String representing the hostname and port of the data node.
     * hostname;port
     * @throws IOException
     */
    private String getLocation(Message req) throws Exception {
        /*if (req.getType() != MessageType.LOCATION) {
            throw new IOException("Bad request type.");
        }*/
        write(req);
        Message loc = read();
        if (loc.getType() == MessageType.ERROR) {
            throw loc.getException();
        } else if (loc.getType() != MessageType.LOCATION) {
            throw new IOException("Bad message type.");
        }
        String path = loc.getPath();
        if (path.length() == 0) {
            throw new IOException("File not found.");
        }
        return path;
    }

    /**
     * Open a socket to the specified data node.
     * @param hoststring hostname:port
     * @return Valid socket.
     * @throws IOException
     */
    private static Socket connectToDataNode(String hoststring)
            throws IOException {
        String[] loc = hoststring.split(":");
        if (loc.length != 2) {
            throw new IOException("Bad host reply.");
        }
        int port;
        try {
            port = Integer.parseInt(loc[1]);
        } catch (NumberFormatException e) {
            throw new IOException("Bad port.");
        }
        return new Socket(loc[0], port);
    }

    /**
     * Reads the entirety of the specified file.
     * @param path File location.
     * @return Contents.
     * @throws IOException
     */
    public String readFile(String path) throws Exception {
        Message getloc = Message.location(path);
        String loc = this.getLocation(getloc);

        Socket node = connectToDataNode(loc);
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());
        Message req = Message.read(path);
        nodeOut.writeObject(req);

        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        Message rep = readReply(nodeIn);
        node.close();

        if (rep.getType() == MessageType.ERROR) {
            throw rep.getException();
        } else if (rep.getType() != MessageType.DATA) {
            throw new IOException("Bad message type.");
        }
        return rep.getPath();
    }

    /**
     * Reads size bytes from the specified file, starting at start.
     * @param path File location.
     * @param start First byte to read.
     * @param size Number of bytes to read.
     * @return Contents.
     * @throws IOException
     */
    public String readBlock(String path, int start, int size)
            throws Exception{
        Message getloc = Message.location(path);
        String loc = this.getLocation(getloc);

        Socket node = connectToDataNode(loc);
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());
        Message req = Message.readBlock(path, start, size);
        nodeOut.writeObject(req);

        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        Message rep = readReply(nodeIn);
        node.close();

        if (rep.getType() == MessageType.ERROR) {
            throw rep.getException();
        } else if (rep.getType() != MessageType.DATA) {
            throw new IOException("Bad message type.");
        }
        return rep.getPath();
    }

    /**
     * Appends output to the specified file. If it does not exist,
     * then it is created on a random data node.
     * @param path File location.
     * @param data Data to append.
     * @throws IOException
     */
    public void writeFile(String path, String data) throws Exception {
        Message getloc = Message.write(path, "");
        String loc = this.getLocation(getloc);
        System.out.println(loc);

        Socket node = connectToDataNode(loc);
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());

        Message req = Message.write(path, data);
        nodeOut.writeObject(req);

        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        Message rep = readReply(nodeIn);
        node.close();

        if (rep.getType() == MessageType.ERROR) {
            throw rep.getException();
        } else if (rep.getType() != MessageType.ACK) {
            throw new IOException("Bad message type.");
        }
    }

    /**
     * Deletes a file from the nameserver and data node.
     * @param path File location.
     * @throws IOException
     */
    public void deleteFile(String path) throws Exception {
        Message msg = Message.delete(path);
        String loc = this.getLocation(msg);

        Socket node = connectToDataNode(loc);
        ObjectOutputStream nodeOut =
                new ObjectOutputStream(node.getOutputStream());
        nodeOut.writeObject(msg);

        ObjectInputStream nodeIn =
                new ObjectInputStream(node.getInputStream());
        Message rep = readReply(nodeIn);
        node.close();

        if (rep.getType() == MessageType.ERROR) {
            throw rep.getException();
        } else if (rep.getType() != MessageType.ACK) {
            throw new IOException("Bad message type.");
        }
    }

    /**
     * Creates an empty file on a particular data node. Note that this only
     * registers the file on the nameserver and doesn't actually create an
     * empty file. So, it would be unwise to read before writing.
     * @param path File location.
     * @param node Data node to use. hostname:port.
     * @throws IOException
     */
    public void createFile(String path, String node) throws Exception {
        Message req = Message.create(path, node);
        write(req);
        Message rep = readReply(in);

        if (rep.getType() == MessageType.ERROR) {
            throw rep.getException();
        } else if (rep.getType() != MessageType.ACK) {
            throw new IOException("Bad message type.");
        }
    }

}
