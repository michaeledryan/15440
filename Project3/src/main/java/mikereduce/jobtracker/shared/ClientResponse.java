package mikereduce.jobtracker.shared;

import java.io.Serializable;

/**
 * Class that represents a message from the JobTracker to the client.
 */
public class ClientResponse implements Serializable{

    private JobState state;
    private String message;

    /**
     * Constructor.
     *
     * @param state State of the job
     * @param message Message for the user.
     */
    public ClientResponse(JobState state, String message) {
        this.state = state;
        this.message = message;
    }

    public JobState getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }



}
