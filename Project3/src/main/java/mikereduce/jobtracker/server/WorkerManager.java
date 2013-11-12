package mikereduce.jobtracker.server;

import mikereduce.shared.ControlMessageType;
import mikereduce.shared.MapContext;
import mikereduce.shared.WorkerControlMessage;
import mikereduce.shared.WorkerJobConfig;
import mikereduce.worker.shared.WorkerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
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

            WorkerMessage woo = null;

            try {
                woo = (WorkerMessage) ois.readObject();
                System.out.println(woo.getStatus());

                switch (woo.getStatus()) {

                    case REGISTRATION:
                        int id = WorkerListener.getInstance().registerWorker(this);
                        info = new WorkerInfo(id, woo.getType()); // Are ID's necessary?

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
                    case HEARTBEAT:
                        // Make me not dead!
                    case UPDATE:
                        // Send data to client running job?
                    case ERROR:
                        // Send error message to client.
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println(woo);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public WorkerType getType() {
        return info.getType();
    }


}
