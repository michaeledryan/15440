package mikereduce.shared;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {

    /**
     * Called at the end of the task.
     */
    abstract protected void cleanup(MapContext context);

    /**
     * Called once per K/V pair.
     */
    abstract protected void map(KEYIN key, VALUEIN val, MapContext context);

    /**
     * Generally not overridden.
     */
    void run(MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> context) {
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
    abstract protected void setup(MapContext context);
}