package mikereduce.shared;

import AFS.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/10/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface InputBlock {

    /**
     *
     * @return The number of bytes in the split
     */
    public long getLength();

    /**
     *
     */
    public String getLine();

    public boolean nextLine();

}
