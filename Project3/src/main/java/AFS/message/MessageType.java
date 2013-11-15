package AFS.message;

/**
 * Categories of messages passed between clients and the DFS.
 */
public enum MessageType {
    READ, READBLOCK, READLINES, COUNTLINES, WRITE, CREATE, DELETE, LOCATION,
    DATA, ACK, ERROR, ADMIN
}
