package mikereduce.worker.mapnode;

import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.shared.ControlMessageType;
import mikereduce.shared.WorkerControlMessage;
import mikereduce.worker.shared.WorkerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Node that runs jobs on data.
 */
public class MapNode implements Runnable {

    private Socket sock;
    private static Set<JobConfig> jobs = new HashSet<JobConfig>();
    private MapperConf conf;
    private int numCores = Runtime.getRuntime().availableProcessors();

    public MapNode(Socket sock, MapperConf conf) {
        this.sock = sock;
        this.conf = conf;
    }

    @Override
    public void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Connect and register
        WorkerMessage connectMessage = WorkerMessage.registration(numCores);

        if (out != null) {
            try {
                out.writeObject(connectMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Wait for an ACK
        WorkerControlMessage ack = null;
        try {
            ack = (WorkerControlMessage) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        // Not sure if this is correct
        if (ack.getType() != ControlMessageType.ACK) {
            System.err.println("Did not receive ACK from JobTracker");
            System.exit(1);
        }

        // Spin up the heartbeat guy
        Timer tim = new Timer();
        final ObjectOutputStream outf = out;
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                try {
                    outf.writeObject(WorkerMessage.heartbeat());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        tim.scheduleAtFixedRate(tt, 0, 6500);

        // We are ready to go!
        // Start the loop that listens for control messages.

        while (true) {
            WorkerControlMessage control;
            try {
                control = (WorkerControlMessage) in.readObject();
                MessageHandler handle = new MessageHandler(control, out);
                new Thread(handle).start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public static Set<JobConfig> getJobs() {
        return jobs;
    }

}