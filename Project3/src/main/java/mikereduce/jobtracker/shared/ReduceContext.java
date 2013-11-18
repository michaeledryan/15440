package mikereduce.jobtracker.shared;

import mikereduce.shared.InputBlock;
import mikereduce.shared.InputFormat;
import mikereduce.shared.OutputCommitter;
import mikereduce.shared.OutputFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Context for a Reducer. Holds input and output channels through which a Reducer performs its tasks.
 */
public class ReduceContext<KEY extends Comparable, VALUE> {


    public Class<? extends Reducer> getReducerClass() {
        return reducerClass;
    }

    private Class<? extends Reducer> reducerClass;
    private OutputCommitter committer;
    private InputBlock reader;
    private KEY currentKey;
    private Iterable<VALUE> currentVals = new ArrayList<>();
    private InputFormat<KEY, VALUE> inputFormat;
    private OutputFormat<KEY, VALUE> outputFormat;

    /**
     * Constructor. Sets relevant fields.
     *
     * @param reducer Object running the reduce tasks.
     * @param out     OutputCommitter through which we send output
     * @param in      InputBlock from which reducer reads input
     */
    public ReduceContext(Reducer<KEY, VALUE> reducer, OutputCommitter out, InputBlock in) {
        this.reducerClass = reducer.getClass();
        this.committer = out;
        reader = in;
        inputFormat = reducer.getInputFormat();
        outputFormat = reducer.getOutputFormat();
    }

    /**
     * @return Whether or not there is another key left in the InputBlock.
     */
    public boolean nextKey() {

        if (!reader.checkNext()) {
            return false;
        }
        KEY newKey = inputFormat.getKey(reader.getLine());
        List<VALUE> newVals = new ArrayList<>();
        newVals.add(inputFormat.getValue(reader.getLine()));

        while (reader.nextLine() && inputFormat.getKey(reader.getLine()).equals(newKey)) {
            newVals.add(inputFormat.getValue(reader.getLine()));
        }

        if (currentVals.equals(newVals) && currentKey.equals(newKey)) {
            return false;
        }
        currentKey = newKey;
        currentVals = newVals;
        return true;
    }

    /**
     * @return the current key.
     */
    public KEY getCurrentKey() {
        return currentKey;
    }

    /**
     * @return All values for the current key.
     */
    public Iterable<VALUE> getValues() {
        return currentVals;
    }

    /**
     * Commits the output of a single reduce operation.
     *
     * @param key key to be written out
     * @param val value to be written out
     */
    public void commit(KEY key, VALUE val) {
        committer.commitLine(outputFormat.parse(key, val), key.hashCode());
    }

}
