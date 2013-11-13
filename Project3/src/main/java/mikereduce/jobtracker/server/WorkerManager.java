package mikereduce.jobtracker.server;

import mikereduce.jobtracker.shared.JobClientStatus;
import mikereduce.jobtracker.shared.JobState;
import mikereduce.shared.ControlMessageType;
import mikereduce.shared.WorkerControlMessage;
import mikereduce.worker.shared.WorkerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class WorkerManager implements Runnable {

    Socket sock;
    WorkerInfo info;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public WorkerManager(Socket worker) {
        sock = worker;
    }

    public void sendRequest(WorkerControlMessage msg) throws IOException {
        oos.writeObject(msg);
    }


    @Override
    public void run() {

        try {
            ois = new ObjectInputStream(sock.getInputStream());
            oos = new ObjectOutputStream(sock.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        while (true) {


            WorkerMessage woo = null;

            try {
                woo = (WorkerMessage) ois.readObject();

                switch (woo.getStatus()) {

                    case REGISTRATION:
                        info = new WorkerInfo(0, woo.getType()); // Are ID's necessary?
                        int id = WorkerListener.getInstance().registerWorker(this);

                        /*
                        Timer tim = new Timer();
                        TimerTask killMe = new TimerTask(){
                            @Override
                            public void run() {
                                // This should mark the worker as "dead."
                            }
                        };

                        tim.schedule(killMe, 20000);
                        */

                        WorkerControlMessage wcm = new WorkerControlMessage(ControlMessageType.ACK, null);
                        oos.writeObject(wcm);

                        // Send ACK
                        break;
                    case HEARTBEAT:
                        // Make me not dead!
                        break;
                    case UPDATE:
                        int percent = woo.getPercent();
                        System.out.println("PERCENT: " + percent);
                        ClientManager client = ClientListener.getInstance().getManager(woo.getJob().getId());

                        JobClientStatus jcs = new JobClientStatus(JobState.COMPLETED, "done");
                        if (percent == 100) {
                            client.sendMessage(jcs);
                            client.reportDone(this);
                        } else {

                        }
                        // Send data to client running job?
                        break;
                    case ERROR:
                        // Send error message to client.
                        break;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public WorkerType getType() {
        return info.getType();
    }


}
