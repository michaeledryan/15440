package mikereduce.shared;

import AFS.Connection;
import mikereduce.worker.mapnode.MapNode;

import java.io.File;
import java.io.IOException;

/**
 * Takes output from a job and writes it to the correct file.
 */
public class OutputCommitter {

    // We assume the output location is unique for each job.
    private String outputPath;
    private Connection conn;

    public OutputCommitter(String path, Connection conn) {
        this.outputPath = path;
        this.conn = conn;
    }

    public void commitLine(String line) {
        try {
            System.out.println("committing");
            conn.writeFile(outputPath, line);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
