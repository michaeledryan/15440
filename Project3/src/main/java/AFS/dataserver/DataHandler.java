package AFS.dataserver;

import AFS.message.StartupMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;

/**
 */
public class DataHandler implements Runnable {

    private String id;
    private int port;
    private String nameServer;
    private int namePort;

    public DataHandler(String id, int port, String nameServer, int namePort) {
        this.id = id;
        this.port = port;
        this.nameServer = nameServer;
        this.namePort = namePort;
    }

    private void initialize() throws IOException {
        File dir = new File(id);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Unable to create directory: " +
                        dir.toString());
            }
        }

        Collection<File> data = FileUtils.listFiles(dir,
                FileFilterUtils.trueFileFilter(),
                FileFilterUtils.trueFileFilter());
        String[] files = new String[data.size()];
        Iterator<File> it = data.iterator();
        for (int i = 0; it.hasNext(); i++) {
            files[i] = it.next().getPath().substring(dir.getPath().length()+1);
        }

        StartupMessage msg = new StartupMessage(InetAddress.getLocalHost()
                .getHostName(), port, files);
        msg.send(nameServer, namePort);
    }

    public void run() {
        try {
            this.initialize();
            new Listener(id, port).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
