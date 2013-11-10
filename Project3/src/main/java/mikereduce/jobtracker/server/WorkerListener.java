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
public class WorkerListener implements Runnable{

    private int port;
    private ServerSocket sock;
    private static WorkerListener INSTANCE;

    private WorkerListener(int port) {
        this.port = port;
    }

    public static void setPort(int workerPort) {
        INSTANCE = new WorkerListener(workerPort);
    }

    public static WorkerListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {
        try {
            sock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        while(true) {
            try {
                Socket client = sock.accept();
                WorkerManager man = new WorkerManager(client);
                Thread t = new Thread(man);
                t.start();

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }

}
