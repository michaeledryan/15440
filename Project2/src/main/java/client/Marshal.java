package client;

import messages.Message;
import util.RemoteObjectRef;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Backend behind the stubs that initializes the RMI and receives the result.
 */
public class Marshal {

    private Socket sock;
    private RemoteObjectRef r;

    public Marshal(RemoteObjectRef r) {
        this.r = r;
    }

    /**
     * Sit around and wait for a reply.
     * @return Object representing the return value.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Message receiveReply() throws IOException, ClassNotFoundException {
        ObjectInputStream inStream = new ObjectInputStream(this.sock
                .getInputStream());
        Object obj = inStream.readObject();
        // TODO: Handle Exception.
        if (!(obj instanceof Message)) {
            throw new IOException("Received object that is not a Message.");
        }
        return (Message) obj;
    }

    /**
     * Send the message to the server.
     * @param m Message of type REQUEST.
     * @throws IOException
     */
    private void sendMessage(Message m) throws IOException {
        ObjectOutputStream outStream = new ObjectOutputStream(this.sock
                .getOutputStream());
        outStream.writeObject(m);
    }

    /**
     * Establish a connection to the server, send the message, and wait
     * for the response.
     * @param meth Name of the method to invoke.
     * @param args Array of argument objects.
     * @param classes Classes of the arguments.
     * @return Returned object.
     * @throws IOException
     */
    public Object run(String meth, Object[] args, Class<?>[] classes)
            throws IOException {
        Object retVal = null;
        try {
            this.sock = new Socket(r.getHost(), r.getPort());
            Message m = Message.newRequest(r.getHost(), r.getPort(), meth,
                    r.getName(), args, classes);
            this.sendMessage(m);
            Message resp = this.receiveReply();
            retVal = resp.getReturnVal();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sock.close();
        }
        if (retVal == null) {
            throw new IOException("retVal is null.");
        } else {
            return retVal;
        }
    }

}
