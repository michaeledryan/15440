package mikereduce.jobtracker.shared;

import mikereduce.shared.InputFormat;
import mikereduce.shared.MapContext;
import mikereduce.shared.OutputFormat;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/10/13
 * Time: 7:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Reducer<KEY extends Comparable, VALUE> {

    /**
     * Called at the end of the task.
     */
    protected void cleanup(ReduceContext context) {
        // To be implemented
    }


    /**
     * Called once per K/V pair.
     */
    protected void reduce(KEY key, Iterable<VALUE> val, ReduceContext context) {
        // To be implemented
    }

    /**
     * Generally not overridden.
     */
    public void run(ReduceContext<KEY, VALUE> context) {
        setup(context);
        try {
            while (context.nextKey()) {
                System.out.println(" REDUCING ");
                reduce(context.getCurrentKey(), context.getValues(), context);
            }
        } finally {
            cleanup(context);
        }
    }

    /**
     * Called before the task runs.
     */
    protected void setup(ReduceContext context) {
        // To be implemented.
    }


    /**
     * OVERRIDE THIS!
     * @return
     */
    public InputFormat<KEY, VALUE> getInputFormat() {
        return null;
    }

    /**
     * OVERRIDE THIS!
     * @return
     */
    public OutputFormat<KEY, VALUE> getOutputFormat() {
        return null;
    }
}
