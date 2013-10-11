package server;

import remote.UnMarshal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 */
public class Listener implements Runnable {

    private int port;
    private ServerSocket socket;
    private ObjectTracker objs;

    public Listener(int port) {
        this.port = port;
        try {
            socket = new ServerSocket(port);
            this.objs = ObjectTracker.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            Socket incoming;
            try {
                incoming = socket.accept();
                System.out.println("Spinning Up Unmarshal");
                UnMarshal handler = new UnMarshal(incoming);
                Thread t = new Thread(handler);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
