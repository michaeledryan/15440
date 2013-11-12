package mikereduce.jobtracker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Listens to worker requests. Keeps track of which workers exist as well as which are Mapper, Reducers, and Combiners.
 *
 */
public class WorkerListener implements Runnable{

    private int port;
    private ServerSocket sock;
    private static WorkerListener INSTANCE;

    private Set<WorkerManager> mappers = new HashSet<WorkerManager>();

    private WorkerListener(int port) {
        this.port = port;
    }

    public static void setPort(int workerPort) {
        INSTANCE = new WorkerListener(workerPort);
    }

    public static WorkerListener getInstance() {
        return INSTANCE;
    }

    public int registerWorker(WorkerManager man) {
        switch (man.getType()) {
            case MAPPER:
                mappers.add(man);
                return mappers.size();
            case REDUCER:
                break;
            case COMBINER:
                break;
        }

        // Shouldn't get here
        return -1;
    }

    public int getNumWorkers() {
        return mappers.size();
    }

    public Set<WorkerManager> getWorkers() {
        return mappers;
    }

    @Override
    synchronized public void run() {
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
