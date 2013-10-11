package server;

import util.Message;
import util.MessageInterpreter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class UnMarshal implements Runnable {

    private Socket sock;
    private Message m;
    private ObjectTracker objs;

    public UnMarshal(Socket sock, ObjectTracker objs) {
        this.sock = sock;
        this.objs = objs;
    }

    private void receiveMessage() throws IOException, ClassNotFoundException {
        ObjectInputStream inStream = new ObjectInputStream(this.sock
                .getInputStream());
        Object obj = inStream.readObject();
        if (!(obj instanceof Message)) {
            throw new IOException("Received object that is not a Message.");
        }
        this.m = (Message) obj;
    }

    private void sendReply() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            this.receiveMessage();
            Object obj = this.objs.lookup(this.m.getName());
            MessageInterpreter mi = new MessageInterpreter(this.m);
            Object res = mi.call();
            this.sendReply();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
