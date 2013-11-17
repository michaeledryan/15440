package mikereduce.shared;

import AFS.Connection;

import java.util.Set;

/**
 * Takes output from a job and writes it to the correct file.
 */
public class OutputCommitter {

    private final int index;
    // We assume the output location is unique for each job.
    private String[] outputPaths;
    private Connection conn;
    private int numPartitions;

    public OutputCommitter(String path, Connection conn, int numPartitions, int index) {
        this.outputPaths = new String[numPartitions];
        this.numPartitions = numPartitions;
        this.conn = conn;
        this.index = index;

        try {
            for (int i = 0; i < numPartitions; i++) {
                outputPaths[i] = path + "_" + index + "," + i;
                conn.writeFile(outputPaths[i], "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOutputPaths(String[] outputPaths) {
        outputPaths[0] = outputPaths[0] + index;
        this.outputPaths = outputPaths;
    }

    /**
     * Writes output line to the AFS.
     * @param line line to be written
     * @param hash used for partitioning
     */
    public void commitLine(String line, int hash) {
        try {
            System.out.println("committing " + line);
            conn.writeFile(outputPaths[hash % outputPaths.length], line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
