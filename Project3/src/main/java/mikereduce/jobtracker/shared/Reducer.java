package mikereduce.jobtracker.shared;

import mikereduce.shared.InputFormat;
import mikereduce.shared.MapContext;
import mikereduce.shared.OutputFormat;

/**
 * This class is extended by a client wishing to use the MapReduce framework.
 * It must implement the reduce task.
 */
public abstract class Reducer<KEY extends Comparable, VALUE> {

    /**
     * Called at the end of the task to handle any final work.
     *
     * @param context Current state of the reduce.
     */
    protected void cleanup(ReduceContext context) {
        // To be implemented
    }

    /**
     * Called once per key, with all instances of the value.
     *
     * @param key     Data key.
     * @param val     All corresponding values.
     * @param context Current state of the reduce.
     */
    protected void reduce(KEY key, Iterable<VALUE> val, ReduceContext context) {
        // To be implemented
    }

    /**
     * Runs a single reduce.
     * Generally NOT overridden.
     *
     * @param context Current state of the reduce.
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
     * Called before the task runs to carry out any initial setup.
     *
     * @param context Current state of the reduce.
     */
    protected void setup(ReduceContext context) {
        // To be implemented.
    }

    /**
     * Implement this to parse the input data.
     *
     * @return Input format.
     */
    public abstract InputFormat<KEY, VALUE> getInputFormat();

    /**
     * Implement this to parse the output format.
     *
     * @return Output format.
     */
    public abstract OutputFormat<KEY, VALUE> getOutputFormat();

}
