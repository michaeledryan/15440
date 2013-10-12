package registry;

import messages.RegistryMessageResponder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Sit and wait for incoming requests.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class Listener {

    private int port;
    private ServerSocket socket;

    public Listener(int port) {
        this.port = port;
        try {
            socket = new ServerSocket(port);
            System.out.printf("Listening on port: %s\n", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sit and run for eternity. Each request is handled in a new Thread.
     */
    public void run() {
        while (true) {
            Socket incoming;
            try {
                incoming = socket.accept();
                RegistryMessageResponder handler =
                        new RegistryMessageResponder(incoming);
                Thread t = new Thread(handler);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
