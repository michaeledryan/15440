package tests.printer;

import remote.Remote440Exception;


public class RemotePrinterImpl implements RemotePrinter {

    public RemotePrinterImpl() {
        super();
    }

    @Override
    public String printMessage(String message) throws Remote440Exception {
        return "YOUR MESSAGE: " + message;
    }


}
