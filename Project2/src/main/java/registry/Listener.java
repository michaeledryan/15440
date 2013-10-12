package registry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import messages.RegistryMessageResponder;

/**
 */
public class Listener {

    private int port;
    private ServerSocket socket;

    public Listener(int port) {
        this.port = port;
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
                System.out.println("CONNECTION!!!");
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
