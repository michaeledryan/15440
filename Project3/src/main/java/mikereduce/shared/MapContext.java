package mikereduce.shared;

/**
 *
 */
public class MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {

    public Class<? extends Mapper> getMapperClass() {
        return mapperClass;
    }

    // Some kind of connection to the host???

    private Class<? extends Mapper> mapperClass;
    private OutputCommitter committer;
    private InputBlock reader;
    private String currentPair;
    private InputFormat<KEYIN, VALUEIN> inputFormat;
    private OutputFormat<KEYOUT, VALUEOUT> outputFormat;

    private long pointInBlock = 0;

    public MapContext(Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> mapper) {
        this.mapperClass = mapper.getClass();
        this.committer = new OutputCommitter(null);
    }

    public boolean nextKeyValue() {
        if (reader.nextLine()) {
            currentPair = reader.getLine();
            pointInBlock = currentPair.length() * 2; // Convert number of chars to bytes. 1 char == 16 bits.
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
        committer.commitLine(outputFormat.parse(key, val));

    }

}