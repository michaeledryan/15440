package mikereduce.shared;

import AFS.Connection;

import java.io.Serializable;

/**
 */
public interface InputBlock extends Serializable{

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
