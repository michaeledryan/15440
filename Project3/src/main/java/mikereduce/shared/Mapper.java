package mikereduce.shared;

/**
 * This class is extended by a client wishing to use the MapReduce framework.
 * It must implement the map task.
 */
public abstract class Mapper<KEYIN extends Comparable, VALUEIN, KEYOUT extends Comparable, VALUEOUT> {

    /**
     * Called at the end of the task to handle any final work.
     *
     * @param context Current state of the map.
     */
    protected void cleanup(MapContext context){

    };

    /**
     * Called once per key, value pair.
     *
     * @param key     Data key.
     * @param val     Data value.
     * @param context Current state of the map.
     */
    protected abstract void map(KEYIN key, VALUEIN val, MapContext context);

    /**
     * Runs a single map.
     * Generally NOT overridden.
     *
     * @param context Current state of the map.
     */
    public void run(MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> context) {
        setup(context);
        try {
            while (context.nextKeyValue()) {
                map(context.getCurrentKey(), context.getCurrentValue(), context);
            }
        } finally {
            context.finishCommit();
            cleanup(context);
        }
    }

    /**
     * Called before the task runs to carry out any initial setup.
     *
     * @param context Current state of the map.
     */
    protected void setup(MapContext context) {

    }

    /**
     * Implement this to parse the input data.
     *
     * @return Input format.
     */
    public abstract InputFormat<KEYIN, VALUEIN> getInputFormat();

    /**
     * Implement this to parse the output format.
     *
     * @return Output format.
     */
    public abstract OutputFormat<KEYOUT, VALUEOUT> getOutputFormat();

}
