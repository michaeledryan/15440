package mikereduce.jobtracker.client;

import mikereduce.jobtracker.shared.JobConfig;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobClient {

    private JobClient(){}

    public static void submit(JobConfig conf, String addr, int port) {
        // Get a connection to the JobTracker and do things.

        Socket sock = null;

        ObjectInputStream ois;
        ObjectOutputStream oos;

        try {
            sock = new Socket(addr, port);

            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());

            oos.writeObject(conf);
            oos.flush();

            while (!sock.isClosed()) {
                Object obj = null;
                obj = ois.readObject();
                System.out.println("getting status....");
                System.out.println(obj);

            }

            System.out.println("job over?");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}