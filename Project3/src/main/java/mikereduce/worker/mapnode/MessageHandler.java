package mikereduce.worker.mapnode;

import AFS.Connection;
import mikereduce.jobtracker.server.JobPhase;
import mikereduce.jobtracker.shared.JobState;
import mikereduce.jobtracker.shared.ReduceContext;
import mikereduce.jobtracker.shared.Reducer;
import mikereduce.shared.*;
import mikereduce.worker.shared.JobStatus;
import mikereduce.worker.shared.WorkerMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Handles a WorkerControlMessage from the JobTracker.
 */
public class MessageHandler implements Runnable {

    private WorkerControlMessage msg;
    private ObjectOutputStream oos;
    private final String address;
    private final int port;

    public MessageHandler(WorkerControlMessage msg, ObjectOutputStream oos) {
        this.msg = msg;
        this.oos = oos;
        this.address = msg.getFSHost();
        this.port = msg.getFSPort();
    }

    @Override
    public void run() {
        // Parse the message! Start the job!
        switch (msg.getType()) {
            case ACK:
                // Don't really need to do anything
                break;
            case NEW:
                // Start the new job
                if (msg.getConfig().getPhase() == JobPhase.MAP) {
                    startMap(msg);
                } else {
                    startReduce(msg);
                }
        }
    }


    /**
     * Spin up a reduce thread
     *
     * @param msg Message with configuration for the reduce.
     */
    private void startReduce(WorkerControlMessage msg) {

        WorkerJobConfig conf = msg.getConfig();
        try {

            // Construct reducer and context
            final Reducer reducer = (Reducer) conf.getConf().getRyaner().newInstance();
            OutputCommitter oc = new OutputCommitter(conf.getOutputLocation(),
                    new Connection(address, port), conf.getNumReducers(), conf.getReducerIndex());
            String[] outPath = new String[1];
            outPath[0] = conf.getOutputLocation();
            oc.setOutputPaths(outPath);
            final ReduceContext rc = new ReduceContext(reducer, oc, conf.getBlock());

            // Run job

            reducer.run(rc);
            WorkerMessage response = WorkerMessage.update(new JobStatus(JobState.COMPLETED, conf.getJobId()), 100);

            // Send response
            oos.writeObject(response);

        } catch (InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Spin up a reduce thread
     *
     * @param msg Message with configuration for the reduce.
     */
    private void startMap(WorkerControlMessage msg) {

        WorkerJobConfig conf = msg.getConfig();

        try {
            // Construct mapper and context.
            final Mapper mapper = (Mapper) conf.getConf().getMiker().newInstance();
            OutputCommitter oc = new OutputCommitter(conf.getOutputLocation(),
                    new Connection(address, port), conf.getNumReducers(), conf.getReducerIndex());
            final MapContext mc = new MapContext(mapper, oc, conf.getBlock());

            // Run job
            mapper.run(mc);

            // Send Response
            WorkerMessage response = WorkerMessage.update(new JobStatus(JobState.COMPLETED, conf.getJobId()), 100);

            oos.writeObject(response);

        } catch (InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }

    }

}
