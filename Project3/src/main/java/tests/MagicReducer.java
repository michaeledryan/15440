package tests;

import mikereduce.jobtracker.shared.ReduceContext;
import mikereduce.jobtracker.shared.Reducer;
import mikereduce.shared.InputFormat;
import mikereduce.shared.OutputFormat;

/**
 * Identity Reduce. Used in the inverted indices example.
 */
public class MagicReducer extends Reducer<String, String> {

    @Override
    protected void reduce(String key, Iterable<String> vals, ReduceContext context) {
        StringBuilder sb = new StringBuilder();
        for (String s : vals) {
            sb.append(s + ",");
        }
        context.commit(key, sb.toString() + "\n");
    }

    @Override
    public InputFormat getInputFormat() {
        return new InputFormat() {
            @Override
            public Object getValue(String currentPair) {
                return currentPair.split(":")[1];
            }

            @Override
            public Comparable getKey(String currentPair) {
                return currentPair.split(":")[0];
            }
        };
    }

    @Override
    public OutputFormat getOutputFormat() {
        return new OutputFormat() {
            @Override
            public String parse(Comparable key, Object val) {
                return key + ":" + val;
            }
        };
    }
}
