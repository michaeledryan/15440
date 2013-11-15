package AFS.message;

import java.io.Serializable;

/**
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -8160308374294717492L;
    private MessageType type;
    private String path = "";
    private String data = null;
    private int start;
    private int size;
    private Exception exception;

    private Message() {
        this.type = MessageType.ACK;
    }

    private Message(MessageType t, String p) {
        this.type = t;
        this.path = p;
    }

    private Message(MessageType t, String p, String d) {
        this.type = t;
        this.path = p;
        this.data = d;
    }

    private Message(MessageType t, String p, int start, int size) {
        this.type = t;
        this.path = p;
        this.start = start;
        this.size = size;
    }

    private Message(MessageType t, Exception exception) {
        this.type = t;
        this.exception = exception;
    }

    public static Message read(String filename) {
        return new Message(MessageType.READ, filename);
    }

    public static Message readBlock(String filename, int start, int size) {
        return new Message(MessageType.READBLOCK, filename, start, size);
    }

    public static Message readLines(String filename, int start, int size) {
        return new Message(MessageType.READLINES, filename, start, size);
    }

    public static Message countLines(String filename) {
        return new Message(MessageType.COUNTLINES, filename);
    }

    public static Message write(String filename, String data) {
        return new Message(MessageType.WRITE, filename, data);
    }

    public static Message create(String filename, String node) {
        return new Message(MessageType.CREATE, filename, node);
    }

    public static Message delete(String filename) {
        return new Message(MessageType.DELETE, filename);
    }

    public static Message location(String loc) {
        return new Message(MessageType.LOCATION, loc);
    }

    public static Message location(String loc, String nodeId) {
        return new Message(MessageType.LOCATION, loc, nodeId);
    }

    public static Message fileContents(String data) {
        return new Message(MessageType.DATA, "", data);
    }

    public static Message ack() {
        return new Message();
    }

    public static Message error(Exception e) {
        return new Message(MessageType.ERROR, e);
    }

    public MessageType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getData() {
        return data;
    }

    public int getStart() {
        return start;
    }

    public int getSize() {
        return size;
    }

    public Exception getException() {
        return exception;
    }
}
