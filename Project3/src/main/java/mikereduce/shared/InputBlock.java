package mikereduce.shared;

import java.io.Serializable;

/**
 */
public interface InputBlock extends Serializable {

    /**
     *
     */
    public String getLine();

    public boolean nextLine();

    public boolean checkNext();

}
