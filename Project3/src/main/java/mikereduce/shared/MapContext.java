package mikereduce.shared;

import mikereduce.jobtracker.shared.JobState;
import mikereduce.worker.mapnode.MessageHandler;
import mikereduce.worker.shared.JobStatus;
import mikereduce.worker.shared.WorkerMessage;

import java.io.IOException;

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
    private int counter = 0;
    private int numSent = 0;
    MessageHandler handler;

    public MapContext(Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> mapper,
                      OutputCommitter out, InputBlock in,
                      MessageHandler handler) {
        this.mapperClass = mapper.getClass();
        this.committer = out;
        this.reader = in;
        this.inputFormat = mapper.getInputFormat();
        this.outputFormat = mapper.getOutputFormat();
        this.handler = handler;
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
        counter++;
        if ((counter % (reader.getLines() / 10)) == 0 && numSent < 10) {
            try {
                handler.sendUpdate();
                numSent++;
            } catch (IOException e) {
                //
            }
        }
    }

    public void finishCommit() {
        committer.finishCommit();
    }
}