package tests.printer;

import remote.Marshal;
import remote.Remote440Exception;
import remote.RemoteObjectRef;
import remote.RemoteStub;

import java.io.IOException;

/**
 * Stub class for RemotePrinters. Handwritten. Marshals invocations, sends them,
 * then waits for the return.
 *
 * @author michaelryan
 */
public class RemotePrinterStub implements RemotePrinter, RemoteStub {

    private static final long serialVersionUID = 7776014088896526651L;
    private RemoteObjectRef remoteRef;

    public RemotePrinterStub() {
        super();
    }

    @Override
    public String printMessage(String string) throws Remote440Exception {
        if (remoteRef == null) {
            throw new Remote440Exception("No RemoteObjectRef specified");
        }

        Marshal mars = new Marshal(remoteRef);

        Class<?>[] clazzes = {String.class};

        Object[] objs = {string};

        Object res = null;
        try {
            res = mars.run("printMessage", objs, clazzes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return (String) res;

    }

    public RemoteObjectRef getRemoteRef() {
        return remoteRef;
    }

    @Override
    public void setRemoteRef(RemoteObjectRef ror) {
        this.remoteRef = ror;
    }

}
