package mikereduce.shared;

/**

 */
public class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {

    /**
     * Called at the end of the task.
     */
    protected void cleanup(MapContext context) {
        // To be implemented
    }


    /**
     * Called once per K/V pair.
     */
    protected void map(KEYIN key, VALUEIN val, MapContext context) {
        // To be implemented
    }

    /**
     * Generally not overridden.
     */
    public void run(MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> context) {
        setup(context);
        try {
            while (context.nextKeyValue()) {
                map(context.getCurrentKey(), context.getCurrentValue(), context);
            }
        } finally {
            cleanup(context);
        }
    }

    /**
     * Called before the task runs.
     */
    protected void setup(MapContext context) {
        // To be implemented.
    }

    ;
}