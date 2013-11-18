package mikereduce.shared;

import AFS.Connection;

/**
 * Takes output from a job and writes it to the correct file.
 */
public class OutputCommitter {

    private final int index;
    // We assume the output location is unique for each job.
    private String[] outputPaths;
    private Connection conn;
    private int commitCount = 0;
    private StringBuilder[] sb;

    /**
     * Constructor. Generates a committer pointed at the given filenames and filesystem.
     *
     * @param path          Base pathname for output
     * @param conn          connection through which the Committer commits.
     * @param numPartitions the number of output partitions for the data.
     * @param index         Used for nomenclature to divide outputs.
     */
    public OutputCommitter(String path, Connection conn, int numPartitions, int index) {
        this.outputPaths = new String[numPartitions];
        this.conn = conn;
        this.index = index;
        sb = new StringBuilder[numPartitions];

        try {
            for (int i = 0; i < numPartitions; i++) {
                outputPaths[i] = path + "_" + index + "," + i;
                conn.writeFile(outputPaths[i], "");
                sb[i] = new StringBuilder();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For a Reduce job, we write to only one output file. Sets the output
     * paths to the specified String[].
     *
     * @param outputPaths the new filename(s) to which output will be written.
     */
    public void setOutputPaths(String[] outputPaths) {
        outputPaths[0] = outputPaths[0] + index;
        this.outputPaths = outputPaths;
    }

    /**
     * Writes output line to the AFS.
     *
     * @param line line to be written
     * @param hash used for partitioning
     */
    public void commitLine(String line, int hash) {
        sb[hash % outputPaths.length].append(line);
        commitCount++;
        if ((commitCount % 100) == 0) {
            try {
                System.out.println("committing " + commitCount);
                conn.writeFile(outputPaths[hash % outputPaths.length], sb[hash % outputPaths.length].toString());
                sb[hash % outputPaths.length] = new StringBuilder();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * If any lines were left out of previous commits, pushes them.
     */
    public void finishCommit() {
        for (int i = 0; i < sb.length; i++) {
            try {
                conn.writeFile(outputPaths[i % outputPaths.length], sb[i].toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
