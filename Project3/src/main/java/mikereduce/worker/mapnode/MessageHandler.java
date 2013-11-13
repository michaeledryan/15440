package mikereduce.worker.mapnode;

import AFS.Connection;
import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.jobtracker.shared.JobState;
import mikereduce.shared.*;
import mikereduce.worker.shared.JobStatus;
import mikereduce.worker.shared.WorkerMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 10:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageHandler implements Runnable {
    // How can I abstract this?

    private WorkerControlMessage msg;
    private ObjectOutputStream oos;

    public MessageHandler(WorkerControlMessage msg, ObjectOutputStream oos) {
        this.msg = msg;
        this.oos = oos;
    }

    @Override
    public void run() {
        // Parse that message! Start the job!
        switch (msg.getType()) {
            case ACK:
                // Uhh, do nothing here? This is for the
                break;
            case KILL:

                break;
            case NEW:
                // Start a new job
                WorkerJobConfig conf = msg.getConfig();

                System.out.println("Trying to get a mapper.");

                try {
                    final Mapper mapper = (Mapper) conf.getConf().getMiker().newInstance();

                    System.out.println(conf.getNumReducers());

                    OutputCommitter oc = new OutputCommitter(conf.getConf().getOutputPath(), new Connection("localhost", 9000), conf.getNumReducers());
                    final MapContext mc = new MapContext(mapper, oc, conf.getBlock());

                    mapper.run(mc);


                    // Report that you're finished.

                    WorkerMessage response = WorkerMessage.update(new JobStatus(JobState.COMPLETED, conf.getJobId()), 100);

                    try {
                        System.out.println("finished map trying to send update back");
                        oos.writeObject(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (InstantiationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


                break;
        }
    }

}
