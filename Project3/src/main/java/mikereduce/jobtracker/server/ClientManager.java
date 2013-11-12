package mikereduce.jobtracker.server;

import AFS.Connection;
import mikereduce.jobtracker.shared.JobClientStatus;
import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.jobtracker.shared.JobState;
import mikereduce.shared.ControlMessageType;
import mikereduce.shared.InputBlock;
import mikereduce.shared.WorkerControlMessage;
import mikereduce.shared.WorkerJobConfig;
import mikereduce.worker.mapnode.AFSInputBlock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

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

            ClientMessage msg = null;

            try {

                msg = (ClientMessage) ois.readObject();
                switch (msg.getType()) {
                    case NEW:
                        startTask(msg.getConf());

                        // Start a new Job.
                        /*
                         * This job needs:
                         *
                         * An input file specified.
                         * An output file specified.
                         * A Mapper class
                         * A Reducer class
                         * A Combiner class
                         *
                         */
                        break;
                    case LIST:
                        // List all running jobs.
                        break;
                }

                // Get config to a thing that runs jobs.
                JobClientStatus jcs = new JobClientStatus(JobState.COMPLETED, "wahh i'm an example!");
                oos.writeObject(jcs);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            System.out.println(msg);
            oos.flush();
            sock.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void startTask(JobConfig conf) {
        // Assume 1 mapper for the time being

        String jobName = UUID.randomUUID().toString();
        Set<WorkerManager> workers = WorkerListener.getInstance().getWorkers();

        InputBlock ib = new AFSInputBlock(conf.getInputPath(), 0, 0);

        for (WorkerManager manager : workers) {

            WorkerJobConfig wjc = new WorkerJobConfig(conf, ib, jobName);
            WorkerControlMessage message = new WorkerControlMessage(ControlMessageType.NEW, wjc);

            try {
                manager.sendRequest(message);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }
}