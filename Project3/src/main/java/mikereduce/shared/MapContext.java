package mikereduce.shared;

/**
 *
 */
public class MapContext<KEYIN extends Comparable, VALUEIN, KEYOUT extends Comparable, VALUEOUT> {

    public Class<? extends Mapper> getMapperClass() {
        return mapperClass;
    }

    private Class<? extends Mapper> mapperClass;
    private OutputCommitter committer;
    private InputBlock reader;
    private String currentPair;
    private InputFormat<KEYIN, VALUEIN> inputFormat;
    private OutputFormat<KEYOUT, VALUEOUT> outputFormat;

    public MapContext(Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> mapper, OutputCommitter out, InputBlock in) {
        this.mapperClass = mapper.getClass();
        this.committer = out;
        reader = in;
        inputFormat = mapper.getInputFormat();
        outputFormat = mapper.getOutputFormat();
    }

    public boolean nextKeyValue() {
        if (reader.nextLine()) {
            currentPair = reader.getLine();
            return true;
        } else return false;
    }

    public KEYIN getCurrentKey() {
        return inputFormat.getKey(currentPair);
    }

    public VALUEIN getCurrentValue() {
        return inputFormat.getValue(currentPair);
    }

    /**
     * Commits the output of a single map operation.
     *
     * @param key key to be written out
     * @param val value to be written out
     */
    public void commit(KEYOUT key, VALUEOUT val) {
        committer.commitLine(outputFormat.parse(key, val), key.hashCode());

    }

    public void finishCommit() {
        committer.finishCommit();
    }
}