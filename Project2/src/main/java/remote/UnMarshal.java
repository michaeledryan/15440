package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;

import messages.RemoteMessage;
import server.ObjectTracker;
import tests.ints.RemoteInteger;

/**
 * Receives a RMI request, runs it, and sends back the result.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class UnMarshal implements Runnable {

    private Socket sock;
    private RemoteMessage m;

    public UnMarshal(Socket sock) {
        this.sock = sock;
    }

    /**
     * Wait for the message to show up, then read it.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void receiveMessage() throws IOException, ClassNotFoundException {
        ObjectInputStream inStream = new ObjectInputStream(
                this.sock.getInputStream());
        Object obj = inStream.readObject();
        if (!(obj instanceof RemoteMessage)) {
            throw new IOException("Received object that is not a "
                    + "RemoteMessage.");
        }
        this.m = (RemoteMessage) obj;
    }

    /**
     * Send back the result.
     *
     * @param resp RemoteMessage of type REPLY or Exception.
     */
    private void sendReply(Object resp) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(
                    this.sock.getOutputStream());
            outStream.writeObject(resp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the local object and run the request.
     */
    public void run() {
        try {
            this.receiveMessage();
            System.out.printf("Received request to invoke method %s(%s) on " +
                    "object %s.\n", m.getMeth(), Arrays.toString(m.getArgs()),
                    m.getName());
            Object res = interpretReply(this.m);
            System.out.println("Request complete.");
            this.sendReply(res);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Object interpretReply(RemoteMessage message) {
        String meth = message.getMeth();
        Class<?>[] clazzes = message.getClasses();

        Object callee = ObjectTracker.getInstance().lookup(message.getName());

        Method calling;
        
        try {
            calling = callee.getClass().getMethod(meth, clazzes);
        } catch (NoSuchMethodException e) {
            return new Remote440Exception(
                    "NoSuchMethodException: could not find method " + meth
                            + " with parameters " + Arrays.toString(clazzes), e);
        } catch (SecurityException e) {
            return new Remote440Exception("Security exception finding method "
                    + meth + " with parameters " + Arrays.toString(clazzes), e);
        }

        Object result;

        try {
            result = calling.invoke(callee, message.getArgs());
        } catch (IllegalAccessException e) {
            return new Remote440Exception("IllegalAccessException finding " +
                    "method "
                    + meth + " with parameters " + Arrays.toString(clazzes), e);
        } catch (IllegalArgumentException e) {
            return new Remote440Exception("Illegal Argument passed to method "
                    + meth + "with parameters " + Arrays.toString(clazzes)
                    + " and arguments " + Arrays.toString(message.getArgs()), e);
        } catch (InvocationTargetException e) {
            return new Remote440Exception("Could not invoke method " + meth
                    + " on object " + message.getName(), e);
        }

        return RemoteMessage.newReply(result);
    }

}
