package mikereduce.jobtracker.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkerManager implements Runnable {

    Socket sock;

    public WorkerManager(Socket worker) {
        sock = worker;
    }
    @Override
    public void run() {

        try {
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

            Object woo = null;

            oos.writeObject("Response");

            try {
                woo = ois.readObject();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println(woo);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
