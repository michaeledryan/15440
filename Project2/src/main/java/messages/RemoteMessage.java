package messages;

import java.io.Serializable;

/**
 * Class for sending messages to invoke remote methods and return their results.
 * <p/>
 * A request is detailed - contains a method name and an array of classes to
 * identify the method being called, an array of arguments for that method, and
 * information about where the message must be sent.
 * <p/>
 * A reply is less detailed - it only contains the return value, represented as
 * an Object for purposes of generality.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class RemoteMessage implements Serializable {

    private static final long serialVersionUID = 8502435830459870335L;
    private MessageType type;
    private String meth = "";
    private String name = "";
    private Object[] args = null;
    private Class<?>[] classes = null;
    private Object returnVal = null;

    /**
     * Private constructors make sure that replies and requests are created
     * correctly via static methods. Default constructor for requests.
     *
     * @param type    Probably should be REQUEST here.
     * @param meth    Method to invoke on the remote object.
     * @param name    The name of the remote object.
     * @param args    Arguments to the function call.
     * @param classes Class types of the arguments.
     */
    private RemoteMessage(MessageType type, String meth,
                          String name, Object[] args, Class<?>[] classes) {
        super();
        this.type = type;
        this.meth = meth;
        this.name = name;
        this.args = args;
        this.classes = classes;
    }

    /**
     * Default constructor for replies.
     *
     * @param type      Probably should be REPLY here.
     * @param returnVal The result of the remote invocation. May be an
     *                  exception.
     */
    private RemoteMessage(MessageType type, Object returnVal) {
        this.type = type;
        this.returnVal = returnVal;
    }

    // Static methods for message construction
    public static RemoteMessage newRequest(String meth,
                                           String name, Object[] args,
                                           Class<?>[] classes) {
        return new RemoteMessage(MessageType.REQUEST, meth, name,
                args, classes);
    }

    public static RemoteMessage newReply(Object returnVal) {
        return new RemoteMessage(MessageType.REPLY, returnVal);
    }

    public String getMeth() {
        return meth;
    }

    public String getName() {
        return name;
    }

    public Object[] getArgs() {
        return args;
    }

    public Class<?>[] getClasses() {
        return classes;
    }

    public Object getReturnVal() {
        return returnVal;
    }

}
