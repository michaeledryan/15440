package client;

import messages.RemoteMessage;
import remote.RemoteObjectRef;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

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
    private RemoteMessage receiveReply() throws IOException, ClassNotFoundException {
        ObjectInputStream inStream = new ObjectInputStream(this.sock
                .getInputStream());
        Object obj = inStream.readObject();
        // TODO: Handle Exception.
        if (!(obj instanceof RemoteMessage)) {
            throw new IOException("Received object that is not a RemoteMessage.");
        }
        return (RemoteMessage) obj;
    }

    /**
     * Send the message to the server.
     * @param m RemoteMessage of type REQUEST.
     * @throws IOException
     */
    private void sendMessage(RemoteMessage m) throws IOException {
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
            RemoteMessage m = RemoteMessage.newRequest(r.getHost(), r.getPort(),
                    meth, r.getName(), args, classes);
            this.sendMessage(m);
            RemoteMessage resp = this.receiveReply();
            retVal = resp.getReturnVal();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            retVal = new IOException("Failed to receive reply.");
        } finally {
            sock.close();
        }
        if (retVal instanceof RemoteException) {
            throw (RemoteException) retVal;
        } else if (retVal instanceof IOException) {
            throw (IOException) retVal;
        } else {
            return retVal;
        }
    }

}
