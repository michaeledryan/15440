package AFS.dataserver;

import AFS.message.Message;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;

/**
 */
public class MessageHandler implements Runnable {

    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String id;

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
            Message m = readMessage();
            Message resp = Message.ack();
            String path = id + File.separator + m.getPath();

            File f = new File(path);
            FileInputStream r;
            FileOutputStream w;

            // TODO: Add failure messages.
            switch (m.getType()) {
                case READ:
                    if (f.exists()) {
                        String data = FileUtils.readFileToString(f, "US-ASCII");
                        resp = Message.fileContents(data);
                    } else {
                        resp = Message.error(
                                new IOException("File not found."));
                    }
                    break;
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
                case DELETE:
                    if (f.exists()) {
                        Boolean b = f.delete();
                        if (!b) {
                            resp = Message.error(
                                    new IOException("Unable to delete file."));
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
