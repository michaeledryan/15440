package mikereduce.worker.mapnode;

import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.shared.MapContext;
import mikereduce.shared.Mapper;
import mikereduce.shared.WorkerControlMessage;

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

                JobConfig conf = msg.getConfig();

                conf.getInputReader();
                conf.getOutputWriter();

                try {
                    final Mapper mapper = (Mapper) conf.getMiker().newInstance();
                    final MapContext mc = new MapContext((Mapper) conf.getMiker().newInstance());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mapper.run(mc);
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
