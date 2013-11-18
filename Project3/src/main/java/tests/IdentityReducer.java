package tests;

import mikereduce.jobtracker.shared.ReduceContext;
import mikereduce.jobtracker.shared.Reducer;
import mikereduce.shared.InputFormat;
import mikereduce.shared.OutputFormat;

/**
 * Simple Reducer. Appends all values together
 */
public class IdentityReducer extends Reducer<String, String> {

    @Override
    /**
     * Called once per K/V pair.
     */
    protected void reduce(String key, Iterable<String> vals, ReduceContext context) {
        StringBuilder sb = new StringBuilder();
        for (String s : vals) {
            sb.append(s);
        }
        context.commit(key, sb.toString());
    }


    /**
     * Split on space.
     *
     * @return
     */
    public InputFormat<String, String> getInputFormat() {
        return new InputFormat<String, String>() {
            @Override
            public String getValue(String currentPair) {
                return currentPair.split(" ")[1];
            }

            @Override
            public String getKey(String currentPair) {
                return currentPair.split(" ")[0];
            }
        };
    }

    /**
     * Extremely simple.
     *
     * @return
     */
    public OutputFormat<String, String> getOutputFormat() {
        return new OutputFormat<String, String>() {
            @Override
            public String parse(String key, String val) {
                return key + " " + val + "\n";
            }
        };
    }
}
