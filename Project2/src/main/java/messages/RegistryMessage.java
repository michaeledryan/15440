package messages;

import remote.Remote440;
import remote.Remote440Exception;

import java.io.Serializable;

/**
 */
public class RegistryMessage implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5720381073680552393L;
	private MessageType type;
	private RegistryMessageType subtype;
	private String name;
	private Remote440 rref;
	private String[] list;
    private Remote440Exception exn;

	private RegistryMessage(MessageType type, RegistryMessageType subtype,
			String name, Remote440 rref) {
		this.type = type;
		this.subtype = subtype;
		this.name = name;
		this.rref = rref;
	}

	private RegistryMessage(MessageType type, RegistryMessageType subtype,
			String name) {
		this.type = type;
		this.subtype = subtype;
		this.name = name;
	}

	private RegistryMessage(MessageType type, RegistryMessageType subtype) {
		this.type = type;
		this.subtype = subtype;
	}

	private RegistryMessage(Remote440 rref, MessageType type) {
		this.rref = rref;
		this.type = type;
	}

	private RegistryMessage(String[] list, MessageType type) {
		this.list = list;
		this.type = type;
	}

	private RegistryMessage() {
		this.type = MessageType.REPLY;
	}

    private RegistryMessage(Remote440Exception exn) {
        this.type = MessageType.REPLY;
        this.subtype = RegistryMessageType.EXN;
        this.exn = exn;
    }

	public static RegistryMessage newBind(String name, Remote440 rref,
			RegistryMessageType subtype) {
		return new RegistryMessage(MessageType.REQUEST, subtype, name, rref);
	}

	public static RegistryMessage newUnBind(String name) {
		return new RegistryMessage(MessageType.REQUEST,
				RegistryMessageType.UNBIND, name);
	}

	public static RegistryMessage newList() {
		return new RegistryMessage(MessageType.REQUEST,
				RegistryMessageType.LIST);
	}

	public static RegistryMessage sendList(String[] list) {
		return new RegistryMessage(list, MessageType.REPLY);
	}

	public static RegistryMessage newLookup(String name) {
		return new RegistryMessage(MessageType.REQUEST,
				RegistryMessageType.LOOKUP, name);
	}

	public static RegistryMessage newReply(Remote440 rref) {
		return new RegistryMessage(rref, MessageType.REPLY);
	}

	public static RegistryMessage newAck() {
		return new RegistryMessage();
	}

    public static RegistryMessage newExn(Remote440Exception exn) {
        return new RegistryMessage(exn);
    }

	public String getName() {
		return name;
	}

	public Remote440 getRref() {
		return rref;
	}

	public RegistryMessageType getSubtype() {
		return subtype;
	}

	public String[] getList() {
		return list;
	}

    public Remote440Exception getExn() {
        return exn;
    }
}
