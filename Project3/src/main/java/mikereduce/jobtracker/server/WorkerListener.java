package mikereduce.jobtracker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Listens to worker requests. Keeps track of which workers are still running.
 */
public class WorkerListener implements Runnable {

    private int port;
    private ServerSocket sock;
    private static WorkerListener INSTANCE;

    private Map<WorkerManager, Integer> mappers = new HashMap<>();

    private WorkerListener(int port) {
        this.port = port;
    }

    public static void setPort(int workerPort) {
        INSTANCE = new WorkerListener(workerPort);
    }

    public static WorkerListener getInstance() {
        return INSTANCE;
    }

    public void registerWorker(WorkerManager man) {
        mappers.put(man, 1);
    }

    public int getNumWorkers() {
        return mappers.size();
    }

    public Map<WorkerManager, Integer> getWorkers() {
        return mappers;
    }

    @Override
    synchronized public void run() {
        try {
            sock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        while (true) {
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


    public void removeWorker(WorkerManager worker) {
        mappers.remove(worker);
    }
}
