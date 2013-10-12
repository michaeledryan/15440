package toys;

import remote.Remote440Exception;


public class ToyClassImpl implements ToyClass {

    public ToyClassImpl() {
        super();
    }

    @Override
    public String printMessage(String message) throws Remote440Exception {
        return "YOUR MESSAGE: " + message;
    }


}
