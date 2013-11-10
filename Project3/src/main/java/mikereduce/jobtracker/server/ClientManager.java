package mikereduce.jobtracker.server;

import mikereduce.jobtracker.shared.JobClientStatus;
import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.jobtracker.shared.JobState;

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
public class ClientManager implements Runnable {

    Socket sock;

    public ClientManager(Socket client) {
        sock = client;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

            JobConfig conf = null;



            try {
                conf = (JobConfig) ois.readObject();

                JobClientStatus jcs = new JobClientStatus(JobState.COMPLETED, "wahh i'm an example!");
                oos.writeObject(jcs);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            System.out.println(conf);
            oos.flush();
            sock.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}