package tests;

import mikereduce.shared.InputFormat;
import mikereduce.shared.MapContext;
import mikereduce.shared.Mapper;
import mikereduce.shared.OutputFormat;

/**
 * Mapper for the small set of Magic: The Gathering card data in Magic.txt.
 * Inverts indices.
 */
public class MagicMapper extends Mapper<String, String, String, String> {

    @Override
    protected void map(String key, String val, MapContext context) {
        context.commit(val, key);
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
                return key + ":" + val + "\n";
            }
        };
    }
}
