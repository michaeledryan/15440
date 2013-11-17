package mikereduce.jobtracker.server;

import mikereduce.jobtracker.shared.JobClientStatus;
import mikereduce.jobtracker.shared.JobState;
import mikereduce.shared.ControlMessageType;
import mikereduce.shared.WorkerControlMessage;
import mikereduce.worker.shared.WorkerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Interface to a worker node.
 */
public class WorkerManager implements Runnable {

    Socket sock;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Set<UUID> jobIds = new ConcurrentSkipListSet<UUID>();

    public WorkerManager(Socket worker) {
        sock = worker;
    }

    /**
     * Sends a WorkerControlMessage to the worker represented
     * @param msg
     * @throws IOException
     */
    public void sendRequest(WorkerControlMessage msg) throws IOException {
        oos.writeObject(msg);
        jobIds.add(msg.getConfig().getJobId());
    }


    @Override
    public void run() {

        try {
            ois = new ObjectInputStream(sock.getInputStream());
            oos = new ObjectOutputStream(sock.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            /**
             * Loop to listen for messages from workers.
             */
            WorkerMessage woo;

            try {
                woo = (WorkerMessage) ois.readObject();

                switch (woo.getStatus()) {

                    case REGISTRATION:
                        WorkerListener.getInstance().registerWorker(this);

                        setupKill();

                        // Send ACK back to worker. This completes registration.
                        WorkerControlMessage wcm = new WorkerControlMessage(ControlMessageType.ACK, null);
                        oos.writeObject(wcm);

                        break;
                    case HEARTBEAT:
                        // After a heartbeat message, keep yourself alive.
                        WorkerListener.getInstance().getWorkers().put(this, 1);
                        break;
                    case UPDATE:
                        // This is updated progress on a job.
                        int percent = woo.getPercent();
                        System.out.println("PERCENT: " + percent);
                        ClientManager client = ClientListener.getInstance().getManager(woo.getJob().getId());

                        JobClientStatus jcs;
                        if (percent == 100) {
                            client.reportDone(this);
                        } else {
                            // What about partial updates?
                        }
                        // Send data to client running job?
                        break;
                    case ERROR:
                        // Send error message to client.
                        break;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a TimerTask to kill the worker in the event of failure.
     */
    private void setupKill() {
        final WorkerManager that = this;

        Timer tim = new Timer();
        TimerTask killMe = new TimerTask() {
            @Override
            public void run() {
                int a = WorkerListener.getInstance().getWorkers().get(that);
                if (a == 0) {
                    // Remove it
                    WorkerListener.getInstance().removeWorker(that);

                    for (UUID jid : jobIds) {
                        ClientListener.getInstance().getManager(jid).reportFailure(that);
                    }

                    System.out.println("Worker at " + sock.getInetAddress() + ":" + sock.getPort() + " died.");
                } else if (a == 1) {
                    WorkerListener.getInstance().getWorkers().put(that, 0);
                }
            }
        };

        tim.scheduleAtFixedRate(killMe, 0, 7000);

    }

}
