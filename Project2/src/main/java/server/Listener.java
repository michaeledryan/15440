package server;

import remote.UnMarshal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listen for requests from clients.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class Listener implements Runnable {

    private int port;
    private ServerSocket socket;
    private ObjectTracker objs;

    public Listener(int port) {
        this.port = port;
        try {
            socket = new ServerSocket(port);
            System.out.printf("Listening on port: %d\n", port);
            this.objs = ObjectTracker.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listen on the socket. Handle requests by UnMarshaling in a new Thread.
     */
    public void run() {
        while (true) {
            Socket incoming;
            try {
                incoming = socket.accept();
                UnMarshal handler = new UnMarshal(incoming);
                Thread t = new Thread(handler);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
