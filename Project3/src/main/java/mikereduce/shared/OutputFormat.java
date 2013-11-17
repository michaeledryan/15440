package mikereduce.shared;

/**
 * Interface for Mappers and reducers to write output.
 */
public interface OutputFormat<KEYOUT extends Comparable, VALUEOUT> {

    String parse(KEYOUT key, VALUEOUT val);
}
