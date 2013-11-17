package mikereduce.jobtracker.server;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

/**
 * Configures the JobTracker.
 */
public class JobTrackerConf {

    private int workerPort; // Port for communication from worker nodes.
    private int clientPort; // Port for listening to clients.


    public JobTrackerConf(File location) {
        Ini ini = new Ini();
        try {
            ini.load(location);
        } catch (IOException e) {
            e.printStackTrace();
        }

        workerPort = ini.get("main", "workerPort", int.class);
        clientPort = ini.get("main", "clientPort", int.class);

    }

    public int getClientPort() {
        return clientPort;
    }

    public int getWorkerPort() {
        return workerPort;
    }
}
