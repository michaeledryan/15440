package mikereduce.jobtracker.client;

import mikereduce.jobtracker.server.ClientMessage;
import mikereduce.jobtracker.shared.JobClientStatus;
import mikereduce.jobtracker.shared.JobState;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Simple point of interaction with the MikeReduce framework.
 * Used by clients to submit a configured job to the framework. This submits
 * the job and receives updates until completion.
 */
public class JobClient {

    // No reason to construct an instance.
    private JobClient() {
    }

    /**
     * Send a job to the framework in order to run it. Receive updates until completion.
     *
     * @param conf the JobConf of the job
     * @param addr address of the JobTracker
     * @param port port of the JobTracker
     */
    public static void submit(ClientMessage conf, String addr, int port) {
        Socket sock;

        ObjectInputStream ois;
        ObjectOutputStream oos;

        try {
            sock = new Socket(addr, port);

            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());

            // Send the config
            oos.writeObject(conf);
            oos.flush();

            // Read responses until the job is completed.
            while (!sock.isClosed()) {
                JobClientStatus message;
                try {
                    message = (JobClientStatus) ois.readObject();
                    System.out.println(message.getMessage());
                    if (message.getState() == JobState.COMPLETED) {
                        break;
                    }

                } catch (EOFException e) {
                    // There is no need to be upset.
                }

            }

            System.out.println("job over?");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}