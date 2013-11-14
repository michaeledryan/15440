package tests;

import mikereduce.shared.InputFormat;
import mikereduce.shared.MapContext;
import mikereduce.shared.Mapper;
import mikereduce.shared.OutputFormat;

public class IdentityMap extends Mapper<String, String, String, String> {

    @Override
    /**
     * Called once per K/V pair.
     */
    protected void map(String key, String val, MapContext context) {
        context.commit(context.getCurrentKey(), context.getCurrentValue());
    }


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
     * OVERRIDE THIS!
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
