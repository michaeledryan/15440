package mikereduce.jobtracker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class ClientListener implements Runnable{

    int port;
    ServerSocket sock;
    private Map<UUID, ClientManager> jobsToClients = new ConcurrentHashMap<>();

    private static  ClientListener INSTANCE;

    private ClientListener(int clientPort) {
        port = clientPort;
        try {
            sock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ClientListener getInstance() {
        return INSTANCE;
    }

    public static ClientListener setupClientListener(int port) {
        INSTANCE = new ClientListener(port);
        return INSTANCE;
    }

    public ClientManager getManager(UUID jobID) {
        return jobsToClients.get(jobID);
    }

    public void addManager(UUID jobId, ClientManager man) {
        jobsToClients.put(jobId, man);
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
                e.printStackTrace();
            }


        }
    }

}
