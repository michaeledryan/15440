package AFS;

import AFS.message.Message;
import AFS.message.MessageType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Establish a connection with the nameserver to have access to the files
 * distributed across the nodes.
 * <p/>
 * It is NOT thread-safe to have multiple concurrent read requests on a single
 * Connection object.
 */
public class Connection implements DistributedIO {

    private Socket s;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private int timeout = 200;

    /**
     * Connects to the nameserver.
     *
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
     *
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
        return (Message) obj;
    }

    /**
     * Reads a reply from the nameserver, opening the InputStream,
     * if necessary.
     *
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
     *
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
     *
     * @param req Message containing the path to look up.
     * @return String representing the hostname and port of the data node.
     *         hostname;port
     * @throws IOException
     */
    private String[] getLocations(Message req) throws Exception {
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
        return path.split(";");
    }

    /**
     * Open a socket to the specified data node.
     *
     * @param hoststring hostname:port
     * @return Valid socket.
     * @throws IOException
     */
    private Socket connectToDataNode(String hoststring)
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
        Socket s = new Socket(loc[0], port);
        s.setSoTimeout(timeout);
        return s;
    }

    private String readRequest(String path, Message req,
                               String nodeId) throws Exception {
        Message getloc = Message.location(path, nodeId);
        String[] locs = this.getLocations(getloc);

        for (String loc : locs) {
            try {
                Socket node = connectToDataNode(loc);
                ObjectOutputStream nodeOut =
                        new ObjectOutputStream(node.getOutputStream());
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
                return rep.getData();
            } catch (Exception e) {

            }
        }
        throw new IOException("No reachable data nodes.");
    }

    /**
     * Reads the entirety of the specified file.
     *
     * @param path File location.
     * @param nodeId Preferred node.
     * @return Contents.
     * @throws Exception
     */
    public String readFile(String path, String nodeId) throws Exception {
        Message req = Message.read(path);
        return readRequest(path, req, nodeId);
    }

    public String readFile(String path) throws Exception {
        return readFile(path, null);
    }

    /**
     * Reads size bytes from the specified file, starting at start.
     *
     * @param path  File location.
     * @param start First byte to read.
     * @param size  Number of bytes to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception
     */
    public String readBlock(String path, int start, int size, String nodeId)
            throws Exception {
        Message req = Message.readBlock(path, start, size);
        return readRequest(path, req, nodeId);
    }

    public String readBlock(String path, int start,
                            int size) throws Exception {
        return readBlock(path, start, size, null);
    }

    /**
     * Reads the specified number of consecutive lines in the file,
     * starting at start.
     *
     * @param path Filename.
     * @param start First line to read.
     * @param size Number of lines to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception
     */
    public String readLines(String path, int start, int size, String nodeId)
            throws Exception {
        Message req = Message.readLines(path, start, size);
        return readRequest(path, req, nodeId);
    }

    public String readLines(String path, int start, int size)
            throws Exception {
        return readLines(path, start, size, null);
    }

    /**
     * Reads the specified line.
     *
     * @param path Filename.
     * @param line Line to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception
     */
    public String readLine(String path, int line, String nodeId)
            throws Exception {
        return readLines(path, line, 1, nodeId);
    }

    public String readLine(String path, int line) throws Exception {
        return readLines(path, line, 1, null);
    }

    /**
     * Gets the number of lines in the file.
     *
     * @param path File name.
     * @param nodeId Preferred data node.
     * @return Line count.
     * @throws Exception
     */
    public int countLines(String path, String nodeId) throws Exception {
        Message req = Message.countLines(path);
        // This is a bit dumb...
        return Integer.parseInt(readRequest(path, req, nodeId));
    }

    public int countLines(String path) throws Exception {
        return countLines(path, null);
    }

    /**
     * Appends output to the specified file. If it does not exist,
     * then it is created on a random data node.
     *
     * @param path File location.
     * @param data Data to append.
     * @throws Exception
     */
    public void writeFile(String path, String data) throws Exception {
        Message getloc = Message.write(path, "");
        String[] locs = this.getLocations(getloc);

        for (String loc : locs) {
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
    }

    /**
     * Deletes a file from the nameserver and data node.
     *
     * @param path File location.
     * @throws Exception
     */
    public void deleteFile(String path) throws Exception {
        Message msg = Message.delete(path);
        String[] locs = this.getLocations(msg);

        for (String loc : locs) {
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
    }

    /**
     * Creates an empty file on a particular data node. Note that this only
     * registers the file on the nameserver and doesn't actually create an
     * empty file. So, it would be unwise to read before writing.
     *
     * @param path File location.
     * @param node Data node to use. hostname:port.
     * @throws Exception
     */
    public void createFile(String path, String node) throws Exception {
        Message req = Message.create(path, node);
        write(req);
        Message rep = read();

        if (rep.getType() == MessageType.ERROR) {
            throw rep.getException();
        } else if (rep.getType() != MessageType.ACK) {
            throw new IOException("Bad message type.");
        }
    }

    /**
     * Copy a local file to the DFS.
     *
     * @param path Filename.
     * @param node Preferred data node.
     * @throws Exception
     */
    public void addLocalFile(File path, String node) throws Exception {
        if (node != null) {
            createFile(path.getPath(), node);
        }
        String data = FileUtils.readFileToString(path, "US-ASCII");
        writeFile(path.getPath(), data);
    }

    public void addLocalFile(File path) throws Exception {
        addLocalFile(path, null);
    }

    public void addLocalFile(String path, String node) throws Exception {
        addLocalFile(new File(path), node);
    }

    public void addLocalFile(String path) throws Exception {
        addLocalFile(new File(path), null);
    }

    /**
     * Copy a group of files to the DFS.
     *
     * @param files Files to copy.
     * @param node Preferred data node.
     * @throws Exception
     */
    public void addLocalFiles(File[] files, String node) throws Exception {
        for (File f : files) {
            addLocalFile(f, node);
        }
    }

    public void addLocalFiles(File[] files) throws Exception {
        addLocalFiles(files, null);
    }

    public void addLocalFiles(String[] files, String node) throws Exception {
        for (String f : files) {
            addLocalFile(new File(f), node);
        }
    }

    public void addLocalFiles(String[] files) throws Exception {
        addLocalFiles(files, null);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
