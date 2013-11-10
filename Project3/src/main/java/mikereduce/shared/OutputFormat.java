package mikereduce.shared;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/10/13
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OutputFormat<KEYOUT, VALUEOUT> {

    String parse(KEYOUT key, VALUEOUT val);
}
