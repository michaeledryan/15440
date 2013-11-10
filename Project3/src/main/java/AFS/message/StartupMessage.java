package AFS.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 */
public class StartupMessage implements Serializable {

    private String hostname;
    private int port;
    private String[] files;

    public StartupMessage(String hostname, int port, String[] files) {
        this.hostname = hostname;
        this.port = port;
        this.files = files;
    }

    public void send(String host, int port) throws IOException {
        Socket s = new Socket(host, port);
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        out.writeObject(this);
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String[] getFiles() {
        return files;
    }
}
