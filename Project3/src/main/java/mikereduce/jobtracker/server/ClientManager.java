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
    ObjectInputStream ois;
    ObjectOutputStream oos;
    private int numWorkers;

    public ClientManager(Socket client) {
        sock = client;
    }

    @Override
    public void run() {

        try {
            ois = new ObjectInputStream(sock.getInputStream());
            oos = new ObjectOutputStream(sock.getOutputStream());

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


            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    //public void update

    private void startTask(JobConfig conf) {
        // Assume 1 mapper for the time being

        UUID jobId = UUID.randomUUID();
        String outputLoc = jobId.toString();
        Set<WorkerManager> workers = WorkerListener.getInstance().getWorkers();

        InputBlock ib = new AFSInputBlock(conf.getInputPath(), 0, 0);
        ClientListener.getInstance().addManager(jobId, this);

        for (WorkerManager manager : workers) {

            WorkerJobConfig wjc = new WorkerJobConfig(conf, ib, outputLoc, jobId, 3);
            WorkerControlMessage message = new WorkerControlMessage(ControlMessageType.NEW, wjc);


            try {
                manager.sendRequest(message);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        JobClientStatus jcs = new JobClientStatus(JobState.RUNNING, "Starts the job.");
        try {
            oos.writeObject(jcs);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(JobClientStatus message) throws IOException {
        oos.writeObject(message);
    }

    /**
     * Report that the Worker finished.
     * @param workerManager
     */
    public void reportDone(WorkerManager workerManager) {

    }
}