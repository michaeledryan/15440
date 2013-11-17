package mikereduce.shared;

/**
 * Interface for Maps and Reducers
 */
public interface InputFormat<KEYIN extends Comparable, VALUEIN> {

    VALUEIN getValue(String currentPair);

    KEYIN getKey(String currentPair);
}
