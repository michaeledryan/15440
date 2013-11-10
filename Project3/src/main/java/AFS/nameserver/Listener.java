package AFS.nameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 */
public class Listener implements Runnable {

    private ServerSocket ln;
    private int nodes;

    public Listener(int port, int nodes) {
        this.nodes = nodes;
        try {
            ln = new ServerSocket(port);
            System.out.printf("Listening on port: %d\n", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        for (int i = 0; i < nodes; i ++) {
            Socket incoming;
            try {
                incoming = ln.accept();
                RegisterNode h = new RegisterNode(incoming);
                Thread t = new Thread(h);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            Socket incoming;
            try {
                incoming = ln.accept();
                MessageHandler h = new MessageHandler(incoming);
                Thread t = new Thread(h);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
