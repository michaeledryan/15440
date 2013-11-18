package AFS.dataserver;

import AFS.management.QueryType;
import AFS.message.Message;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;

/**
 * Responds to incoming messages to the data node.
 */
public class MessageHandler implements Runnable {

    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String id;

    /**
     * Open I/O streams.
     *
     * @param id This node's data directory.
     * @param s  Client socket.
     */
    public MessageHandler(String id, Socket s) {
        this.id = id;
        try {
            this.s = s;
            this.in = new ObjectInputStream(s.getInputStream());
            this.out = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a message from the socket.
     *
     * @return Received message.
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
     * Reads and responds to a single message.
     */
    public void run() {
        try {
            Message m = readMessage();
            FileCache cache = FileCache.getInstance();
            // Default reply. Overwritten where necessary.
            Message resp = Message.ack();
            String path = id + File.separator + m.getPath();

            File f = new File(path);
            File dir;
            FileInputStream r;
            FileOutputStream w;

            switch (m.getType()) {

                // Send back file contents.
                case READ:
                    if (f.exists()) {
                        resp = Message.fileContents(cache.read(path));
                    } else {
                        resp = Message.error(
                                new IOException("File not found."));
                    }
                    break;

                // Send back the specified portion of the file.
                case READBLOCK:
                    if (f.exists()) {
                        r = new FileInputStream(path);
                        byte[] buf = new byte[m.getSize()];
                        r.skip(m.getStart());
                        int n = r.read(buf);
                        if (n == m.getSize()) {
                            resp = Message.fileContents(new String(buf));
                        } else {
                            resp = Message.error(
                                    new IOException("Could not read " +
                                            "requested block."));
                        }
                    } else {
                        resp = Message.error(
                                new IOException("File not found."));
                    }
                    break;

                // Send back the specified portion of the file.
                case READLINES:
                    if (f.exists()) {
                        resp = Message.fileContents(
                                cache.readLines(path, m.getStart(),
                                        m.getSize()));
                    } else {
                        resp = Message.error(
                                new IOException("File not found."));
                    }
                    break;

                // Counts the number of lines.
                case COUNTLINES:
                    if (f.exists()) {
                        int count = cache.countLines(path);
                        resp = Message.fileContents(Integer.toString(count));
                    } else {
                        resp = Message.error(
                                new IOException("File not found."));
                    }
                    break;

                // Append to the file (created if it doesn't exist).
                case WRITE:
                    cache.write(path, m.getData());
                    break;

                // Remove the file.
                case DELETE:
                    if (f.exists()) {
                        cache.remove(path);
                        Boolean b = f.delete();
                        if (!b) {
                            resp = Message.error(
                                    new IOException("Unable to delete file."));
                        }
                        f = f.getParentFile();
                        while (b && f.isDirectory() && f.list().length == 0) {
                            b = f.delete();
                            f = f.getParentFile();
                        }

                    } else {
                        resp = Message.error(
                                new IOException("File not found."));
                    }
                    break;

                case ADMIN:
                    try {
                        switch (QueryType.fromString(m.getPath())) {
                            case FILES:
                                dir = new File(id);
                                Collection<File> data = FileUtils.listFiles(dir,
                                        FileFilterUtils.trueFileFilter(),
                                        FileFilterUtils.trueFileFilter());
                                String res = "Filename\n";
                                res += StringUtils.repeat("-", 80) + "\n";
                                Iterator<File> it = data.iterator();
                                while (it.hasNext()) {
                                    res += it.next().getPath().substring(dir
                                            .getPath().length() + 1) + "\n";
                                }
                                resp = Message.fileContents(res);
                                break;

                            default:
                                resp = Message.error(
                                        new Exception("Unknown task for " +
                                                "data node: " + m.getPath()));
                                break;
                        }
                    } catch (Exception e) {
                        resp = Message.error(e);
                    }

                default:
                    break;
            }
            out.writeObject(resp);
            s.close();
        } catch (IOException e) {
            Message resp = Message.error(e);
            try {
                out.writeObject(resp);
            } catch (IOException ex) {
                e.printStackTrace();
                ex.printStackTrace();
            }
        }
    }

}
