package mikereduce.jobtracker.shared;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobClientStatus implements Serializable{

    private JobState state;
    private String message;

    public JobClientStatus(JobState state, String message) {
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
