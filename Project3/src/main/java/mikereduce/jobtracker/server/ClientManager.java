package mikereduce.jobtracker.server;

import AFS.Connection;
import mikereduce.jobtracker.shared.JobClientStatus;
import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.jobtracker.shared.JobState;
import mikereduce.shared.ControlMessageType;
import mikereduce.shared.InputBlock;
import mikereduce.shared.WorkerControlMessage;
import mikereduce.shared.WorkerJobConfig;
import mikereduce.worker.mapnode.AFSInputBlock;
import mikereduce.worker.mapnode.AFSReduceInputBlock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles a single client's task.
 */
public class ClientManager implements Runnable {

    private Socket sock;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private int numMappers;
    private JobConfig jc;
    private UUID jobId;
    private JobPhase phase;
    private Map<WorkerManager, Integer> workers = new ConcurrentHashMap<>();
    private PriorityQueue<Integer> remainingIndices = new PriorityQueue<>();
    private JobConfig conf;
    private int numReducers;

    public ClientManager(Socket client) {
        sock = client;
    }

    @Override
    public void run() {

        try {
            ois = new ObjectInputStream(sock.getInputStream());
            oos = new ObjectOutputStream(sock.getOutputStream());

            ClientMessage msg;

            try {

                msg = (ClientMessage) ois.readObject();
                switch (msg.getType()) {
                    case NEW:
                        startMap(msg.getConf());

                        // Start a new Job.
                        /*
                         * This job needs:
                         *
                         * An input file specified.
                         * An output file specified.
                         * A Mapper class
                         * A Reducer class
                         *
                         */
                        break;
                    case LIST:
                        // List all running jobs.
                        break;
                }

                // Get config to a thing that runs jobs.


            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Takes a configuration for a job, then spins up the task.
     *
     * @param conf a configuration from the client
     */
    private void startMap(JobConfig conf) {
        // Assume 1 mapper for the time being
        this.conf = conf;
        phase = JobPhase.MAP;
        jobId = UUID.randomUUID();
        String outputLoc = jobId.toString();
        Map<WorkerManager, Integer> workers = WorkerListener.getInstance().getWorkers();

        int lineCount = 0;
        try {
            lineCount = new Connection("localhost", 9000).countLines(conf.getInputPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClientListener.getInstance().addManager(jobId, this);

        jc = conf;

        numReducers = conf.getNumReducers();
        if (numReducers == 0) numReducers = 4;

        numMappers = conf.getNumMappers();
        if (numMappers == 0) {
            numMappers = 4; // just cause ???
        }
        List<WorkerManager> myWorkers = new ArrayList<>();

        for (WorkerManager manager : workers.keySet()) {
            if (myWorkers.size() == numMappers) {
                break;
            }
            myWorkers.add(manager);
        }

        numMappers = myWorkers.size();

        for (int i = 0; i < numMappers; i++) {

            int startLine = i * lineCount / numMappers;

            int endLine = (i != numMappers - 1) ? lineCount / numMappers : lineCount - i * lineCount/numMappers;

             InputBlock ib = new AFSInputBlock(conf.getInputPath(), startLine, endLine, "localhost", 9000); //TODO: MAKE REAL

            WorkerManager manager = myWorkers.get(i);
            WorkerJobConfig wjc = new WorkerJobConfig(conf, ib, outputLoc, jobId, numReducers, i, phase);
            WorkerControlMessage message = new WorkerControlMessage(ControlMessageType.NEW, wjc);

            this.workers.put(manager, i);

            try {
                manager.sendRequest(message);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        JobClientStatus jcs = new JobClientStatus(JobState.RUNNING, "Started the job with id: " + jobId.toString());
        try {
            oos.writeObject(jcs);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends a response to the client that started the job.
     *
     * @param message Message to be sent.
     * @throws IOException If there is a communication problem.
     */
    public void sendMessage(JobClientStatus message) throws IOException {
        oos.writeObject(message);
        oos.flush();
    }

    /**
     * Report that the Worker finished.
     *
     * @param workerManager
     */
    public void reportDone(WorkerManager workerManager) {

        if (!remainingIndices.isEmpty()) {
            InputBlock ib = new AFSInputBlock(jc.getInputPath(), 0, 0, "localhost", 9000); //TODO: MAKE REAL

            WorkerJobConfig wjc = new WorkerJobConfig(jc, ib, jobId.toString(), jobId, 1, remainingIndices.poll(), phase);
            WorkerControlMessage message = new WorkerControlMessage(ControlMessageType.NEW, wjc);
            try {
                workerManager.sendRequest(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            workers.remove(workerManager);

            // if (workers.isEmpty() && phase == JobPhase.MAP) {
            if (phase == JobPhase.MAP) {
                JobClientStatus jcs = new JobClientStatus(JobState.RUNNING, "Finished map phase");
                try {
                    sendMessage(jcs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (workers.isEmpty()) {
                    startReduce();
                }
            } else {
                JobClientStatus jcs = new JobClientStatus(JobState.COMPLETED, "Finished map phase");
                try {
                    sendMessage(jcs);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Runs the Reduce portion of a job.
     */
    private void startReduce() {

        System.out.println("\t Trying to reduce.");
        String outputLoc = conf.getOutputPath();
        Map<WorkerManager, Integer> workers = WorkerListener.getInstance().getWorkers();

        phase = JobPhase.REDUCE;

        ClientListener.getInstance().addManager(jobId, this);

        List<WorkerManager> myWorkers = new ArrayList<>();

        for (WorkerManager manager : workers.keySet()) {
            if (myWorkers.size() == numReducers) {
                break;
            }
            myWorkers.add(manager);
        }


        int i = 0;
        for (WorkerManager manager : myWorkers) {

            Set<String> intermediateFiles = new HashSet<>();

            for (int j = 0; j < numMappers; j++) {
                intermediateFiles.add(jobId.toString() + "_" + j + "," + i);
            }

            InputBlock ib = new AFSReduceInputBlock(intermediateFiles, "localhost", 9000);

            WorkerJobConfig wjc = new WorkerJobConfig(conf, ib, outputLoc, jobId, numMappers, i, phase);
            WorkerControlMessage message = new WorkerControlMessage(ControlMessageType.NEW, wjc);

            this.workers.put(manager, i++);

            try {
                manager.sendRequest(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JobClientStatus jcs = new JobClientStatus(JobState.RUNNING, "Starting reduce.");
        try {
            oos.writeObject(jcs);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Report the failure of a worker node to the ClientManager.
     * We compensate for this by running an additional map on the first node that clears up.
     *
     * @param workerManager failed worker
     */
    public void reportFailure(WorkerManager workerManager) {
        System.out.println("\t" + workers.keySet());
        System.out.println("\t" + workerManager);
        remainingIndices.add(workers.get(workerManager));
        workers.remove(workerManager);
    }
}