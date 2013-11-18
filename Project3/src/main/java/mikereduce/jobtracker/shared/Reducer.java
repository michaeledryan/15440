package mikereduce.jobtracker.shared;

import mikereduce.shared.InputFormat;
import mikereduce.shared.MapContext;
import mikereduce.shared.OutputFormat;

/**
 * User-implemented class for running Reduce tasks.
 */
public class Reducer<KEY extends Comparable, VALUE> {

    /**
     * Called at the end of the task.
     */
    protected void cleanup(ReduceContext context) {
        context.finishCommit();
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
     * Gets the InputFormat for the Mapper.
     * Override this.
     * @return an InputFormat to parse input.
     */
    public InputFormat<KEY, VALUE> getInputFormat() {
        return null;
    }

    /**
     * Gets the OutputFormat for the Mapper.
     * Override this.
     * @return an OutputFormat to parse input.
     */
    public OutputFormat<KEY, VALUE> getOutputFormat() {
        return null;
    }
}
