package mikereduce.jobtracker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientListener implements Runnable{

    int port;
    ServerSocket sock;

    public ClientListener(int clientPort) {
        port = clientPort;
        try {
            sock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket client = sock.accept();
                ClientManager man = new ClientManager(client);
                Thread t = new Thread(man);
                t.start();

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }

}
