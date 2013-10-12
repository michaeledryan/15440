package messages;

import java.io.Serializable;

/**
 * Class for sending messages to invoke remote methods and return their results.
 * 
 * A request is detailed - contains a method name and an array of classes to
 * identify the method being called, an array of arguments for that method, and
 * information about where the message must be sent.
 * 
 * A reply is less detailed - it only contains the return value, represented as
 * an Object for purposes of generality.
 * 
 * @author michaelryan
 * 
 */
public class RemoteMessage implements Serializable {

	// Autogenerated.
	private static final long serialVersionUID = 8502435830459870335L;
	private String host = "";
	private int port = 1099;
	private MessageType type;
	private String meth = "";
	private String name = "";
	private Object[] args = null;
	private Class<?>[] classes = null;
	private Object returnVal = null;

	// Private constructors make sure that replies and requests are created
	// correctly via static methods.
	private RemoteMessage(String host, int port, MessageType type, String meth,
			String name, Object[] args, Class<?>[] classes) {
		super();
		this.host = host;
		this.port = port;
		this.type = type;
		this.meth = meth;
		this.name = name;
		this.args = args;
		this.classes = classes;
	}

	private RemoteMessage(MessageType type, Object returnVal) {
		// Maybe here is where to switch up reference and value passing?
		this.type = type;
		this.returnVal = returnVal;
	}

	// Static methods for message construction
	public static RemoteMessage newRequest(String host, int port, String meth,
			String name, Object[] args, Class<?>[] classes) {
		return new RemoteMessage(host, port, MessageType.REQUEST, meth, name,
				args, classes);
	}

	public static RemoteMessage newReply(Object returnVal) {
		return new RemoteMessage(MessageType.REPLY, returnVal);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMeth() {
		return meth;
	}

	public void setMeth(String meth) {
		this.meth = meth;
	}

	public MessageType getType() {
		return this.type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Class<?>[] getClasses() {
		return classes;
	}

	public void setClasses(Class<?>[] classes) {
		this.classes = classes;
	}

	public Object getReturnVal() {
		return returnVal;
	}

	public void setReturnVal(Object returnVal) {
		this.returnVal = returnVal;
	}

}
