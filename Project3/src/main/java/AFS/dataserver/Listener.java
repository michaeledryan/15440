package AFS.dataserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 */
public class Listener {

    private String id;
    private ServerSocket ln;

    public Listener(String id, int port) {
        this.id = id;
        try {
            ln = new ServerSocket(port);
            System.out.printf("Listening on port: %d\n", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            Socket incoming;
            try {
                incoming = ln.accept();
                MessageHandler h = new MessageHandler(id, incoming);
                Thread t = new Thread(h);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
