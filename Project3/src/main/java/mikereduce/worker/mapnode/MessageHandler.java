package mikereduce.worker.mapnode;

import AFS.Connection;
import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.shared.*;

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

    public MessageHandler(WorkerControlMessage msg) {
        this.msg = msg;
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

                    OutputCommitter oc = new OutputCommitter(conf.getConf().getOutputPath(), new Connection("localhost", 9001));
                    final MapContext mc = new MapContext(mapper, oc);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mapper.run(mc);
                            System.out.println("DONE BITCHES");
                            // Report that you're finished.
                        }
                    });

                } catch (InstantiationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


                break;
        }
    }

}
