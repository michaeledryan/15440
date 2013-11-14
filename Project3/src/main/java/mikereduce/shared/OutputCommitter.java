package mikereduce.shared;

import AFS.Connection;

/**
 * Takes output from a job and writes it to the correct file.
 */
public class OutputCommitter {

    // We assume the output location is unique for each job.
    private String[] outputPaths;
    private Connection conn;
    private int numPartitions;

    public OutputCommitter(String path, Connection conn, int numPartitions) {
        this.outputPaths = new String[numPartitions];
        this.numPartitions = numPartitions;
        this.conn = conn;

        try {
            for (int i = 0; i < numPartitions; i++) {
                outputPaths[i] = path + "_" + i;
                conn.createFile(outputPaths[i], "michaels-air:8002");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commitLine(String line, int hash) {
        try {
            System.out.println("committing " + line);
            conn.writeFile(outputPaths[hash % numPartitions], line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
