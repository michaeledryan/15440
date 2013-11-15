package AFS.dataserver;

import AFS.message.Message;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
     * @param s Client socket.
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
            // Default reply. Overwritten where necessary.
            Message resp = Message.ack();
            String path = id + File.separator + m.getPath();

            File f = new File(path);
            FileInputStream r;
            FileOutputStream w;

            switch (m.getType()) {

                // Send back file contents.
                case READ:
                    if (f.exists()) {
                        String data = FileUtils.readFileToString(f, "US-ASCII");
                        resp = Message.fileContents(data);
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
                        String data = FileUtils.readFileToString(f, "US-ASCII");
                        String[] lines = data.split("\n");
                        String contents = "";
                        int limit = m.getStart() + m.getSize();
                        Boolean abort = false;

                        for (int i = m.getStart(); i < limit; i++) {
                            if (i >= lines.length) {
                                abort = true;
                                break;
                            }
                            contents += lines[i];
                            if (i != limit - 1) {
                                contents += "\n";
                            }
                        }

                        if (abort) {
                            resp = Message.error(
                                    new IOException("Lines out of range."));
                        } else {
                            resp = Message.fileContents(contents);
                        }
                    } else {
                        resp = Message.error(
                                new IOException("File not found."));
                    }
                    break;

                // Append to the file (created if it doesn't exist).
                case WRITE:
                    File dir = new File(f.getParent());
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            resp = Message.error(
                                    new IOException("Failed to create parent " +
                                            "directories."));
                        }
                    }
                    w = new FileOutputStream(path, true);
                    w.write(m.getData().getBytes());
                    w.close();
                    break;

                // Remove the file.
                case DELETE:
                    if (f.exists()) {
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
