package mikereduce.shared;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/10/13
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface InputFormat<KEYIN, VALUEIN> {

    VALUEIN getValue(String currentPair);

    KEYIN getKey(String currentPair);
}
