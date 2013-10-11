package registry;

import messages.RegistryMessageResponder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 */
public class Listener {

    private int port;
    private RrefTracker refs;
    private ServerSocket socket;

    public Listener(int port, RrefTracker refs) {
        this.port = port;
        this.refs = refs;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            Socket incoming;
            try {
                incoming = socket.accept();
                RegistryMessageResponder interp = new
                        RegistryMessageResponder(incoming, refs);
                Thread t = new Thread(interp);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
