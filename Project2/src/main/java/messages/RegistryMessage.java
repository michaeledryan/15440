package messages;

import remote.Remote440Exception;
import remote.RemoteObjectRef;

import java.io.Serializable;

/**
 * Registry messages are used to communicate with the registry.
 * The client uses it for LOOKUP/LIST.
 * The server uses it for BIND/REBIND/UNBIND.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class RegistryMessage implements Serializable {

    private static final long serialVersionUID = -5720381073680552393L;
    private MessageType type;
    private RegistryMessageType subtype;
    private String name;
    private RemoteObjectRef rref;
    private String[] list;
    private Remote440Exception exn;

    /*
     * Constructors are private. Use static methods to provide new messages.
     * They're all very similar...
     */
    private RegistryMessage(MessageType type, RegistryMessageType subtype,
                            String name, RemoteObjectRef rref) {
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

    private RegistryMessage(RemoteObjectRef rref, MessageType type) {
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

    /**
     * Message to request a new bind or rebind.
     *
     * @param name    Name of the remote object.
     * @param rref    Remote object to register.
     * @param subtype Must be BIND or REBIND.
     * @return The message.
     */
    public static RegistryMessage newBind(String name, RemoteObjectRef rref,
                                          RegistryMessageType subtype) {
        return new RegistryMessage(MessageType.REQUEST, subtype, name, rref);
    }

    /**
     * Message to request an unbind.
     *
     * @param name Name of the remote object.
     * @return The message.
     */
    public static RegistryMessage newUnBind(String name) {
        return new RegistryMessage(MessageType.REQUEST,
                RegistryMessageType.UNBIND, name);
    }

    /**
     * Message to request a list of registered objects.
     *
     * @return The message.
     */
    public static RegistryMessage newList() {
        return new RegistryMessage(MessageType.REQUEST,
                RegistryMessageType.LIST);
    }

    /**
     * Message to respond to a LIST request.
     *
     * @param list List of all registered objects.
     * @return The message.
     */
    public static RegistryMessage sendList(String[] list) {
        return new RegistryMessage(list, MessageType.REPLY);
    }

    /**
     * Message to request a lookup of a remote object.
     *
     * @param name Name of the remote object.
     * @return The message.
     */
    public static RegistryMessage newLookup(String name) {
        return new RegistryMessage(MessageType.REQUEST,
                RegistryMessageType.LOOKUP, name);
    }

    /**
     * Message responding to a successful lookup.
     *
     * @param rref The remote object reference (may be null).
     * @return The message.
     */
    public static RegistryMessage newReply(RemoteObjectRef rref) {
        return new RegistryMessage(rref, MessageType.REPLY);
    }

    /**
     * BIND, REBIND, and UNBIND just get a success acknowledgement.
     *
     * @return The message.
     */
    public static RegistryMessage newAck() {
        return new RegistryMessage();
    }

    /**
     * Message handing back a Remote440Exception.
     *
     * @param exn Exception encountered during the request.
     * @return The message.
     */
    public static RegistryMessage newExn(Remote440Exception exn) {
        return new RegistryMessage(exn);
    }

    public String getName() {
        return name;
    }

    public RemoteObjectRef getRref() {
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
