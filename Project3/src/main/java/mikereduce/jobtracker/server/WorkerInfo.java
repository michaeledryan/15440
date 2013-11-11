package mikereduce.jobtracker.server;

import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/10/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkerInfo {

    private int id;
    private WorkerType type;

    public WorkerInfo(int id, WorkerType type) {
    }

    public int getId() {
        return id;
    }

    public WorkerType getType() {
        return type;
    }
}
