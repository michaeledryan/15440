package AFS;

/**
 * Interface to the distributed file system. All client-side IO can be done
 * with these.
 */
public interface DistributedIO {

    /**
     * Reads the entirety of the specified file.
     *
     * @param path File location.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception
     */
    public String readFile(String path, String nodeId) throws Exception;
    public String readFile(String path) throws Exception;

    /**
     * Reads size bytes from the specified file, starting at start.
     *
     * @param path  File location.
     * @param start First byte to read.
     * @param size  Number of bytes to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception
     */
    public String readBlock(String path, int start, int size, String nodeId)
            throws Exception;
    public String readBlock(String path, int start, int size) throws Exception;

    /**
     * Reads the specified number of consecutive lines in the file,
     * starting at start.
     *
     * @param path Filename.
     * @param start First line to read.
     * @param size Number of lines to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception
     */
    public String readLines(String path, int start, int size, String nodeId)
            throws Exception;
    public String readLines(String path, int start, int size)
            throws Exception;

    /**
     * Reads the specified line.
     *
     * @param path Filename.
     * @param line Line to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception
     */
    public String readLine(String path, int line, String nodeId)
            throws Exception;
    public String readLine(String path, int line) throws Exception;

    /**
     * Gets the number of lines in the file.
     *
     * @param path File name.
     * @param nodeId Preferred data node.
     * @return Line count.
     * @throws Exception
     */
    public int countLines(String path, String nodeId) throws Exception;
    public int countLines(String path) throws Exception;

    /**
     * Appends output to the specified file. If it does not exist,
     * then it is created on a random data node.
     *
     * @param path File location.
     * @param data Data to append.
     * @throws Exception
     */
    public void writeFile(String path, String data) throws Exception;

    /**
     * Deletes a file from the nameserver and data node.
     *
     * @param path File location.
     * @throws Exception
     */
    public void deleteFile(String path) throws Exception;

    /**
     * Creates an empty file on a particular data node. Note that this only
     * registers the file on the nameserver and doesn't actually create an
     * empty file. So, it would be unwise to read before writing.
     *
     * @param path File location.
     * @param node Data node to use. hostname:port.
     * @throws Exception
     */
    public void createFile(String path, String node) throws Exception;

}
