package AFS.dataserver;

import AFS.message.Message;

import java.io.*;
import java.net.Socket;

import org.apache.commons.io.FileUtils;

/**
 */
public class MessageHandler implements Runnable {

    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MessageHandler(Socket s) {
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
        return (Message)obj;
    }

    public void run() {
        try {
            Message m = readMessage();
            Message resp = Message.ack();
            String path = m.getPath();

            File f;
            FileInputStream r;
            FileOutputStream w;

            switch (m.getType()) {
                case READ:
                    // TODO: Need to be in worker's directory.
                    f = new File(path);
                    String data = FileUtils.readFileToString(f, "US-ASCII");
                    resp = Message.fileContents(data);
                    break;
                case READBLOCK:
                    r = new FileInputStream(path);
                    byte[] buf = new byte[m.getSize()];
                    r.read(buf, m.getStart(), m.getSize());
                    resp = Message.fileContents(new String(buf));
                    break;
                case WRITE:
                    w = new FileOutputStream(path, true);
                    w.write(m.getData().getBytes());
                    break;
                case DELETE:
                    f = new File(path);
                    if (f.exists()) {
                        f.delete();
                    }
                    break;
                default:
                    break;
            }
            out.writeObject(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
