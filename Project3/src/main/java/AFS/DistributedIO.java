package AFS;

import java.io.File;

/**
 * Interface to the distributed file system. All client-side IO can be done
 * with these.
 * <p/>
 * Overloaded functions with the optional node parameter, as well as createFile,
 * allows the client to specify a preferred data node.
 */
public interface DistributedIO {

    /**
     * Reads the entirety of the specified file.
     *
     * @param path   File location.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readFile(String path, String nodeId) throws Exception;

    /**
     * Reads the entirety of the specified file.
     *
     * @param path File location.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readFile(String path) throws Exception;

    /**
     * Reads size bytes from the specified file, starting at start.
     *
     * @param path   File location.
     * @param start  First byte to read.
     * @param size   Number of bytes to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readBlock(String path, int start, int size, String nodeId)
            throws Exception;

    /**
     * Reads size bytes from the specified file, starting at start.
     *
     * @param path  File location.
     * @param start First byte to read.
     * @param size  Number of bytes to read.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readBlock(String path, int start, int size) throws Exception;

    /**
     * Reads the specified number of consecutive lines in the file,
     * starting at start.
     *
     * @param path   Filename.
     * @param start  First line to read.
     * @param size   Number of lines to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readLines(String path, int start, int size, String nodeId)
            throws Exception;

    /**
     * Reads the specified number of consecutive lines in the file,
     * starting at start.
     *
     * @param path  Filename.
     * @param start First line to read.
     * @param size  Number of lines to read.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readLines(String path, int start, int size)
            throws Exception;

    /**
     * Reads the specified line.
     *
     * @param path   Filename.
     * @param line   Line to read.
     * @param nodeId Preferred data node.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readLine(String path, int line, String nodeId)
            throws Exception;

    /**
     * Reads the specified line.
     *
     * @param path Filename.
     * @param line Line to read.
     * @return Contents.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public String readLine(String path, int line) throws Exception;

    /**
     * Gets the number of lines in the file.
     *
     * @param path   File name.
     * @param nodeId Preferred data node.
     * @return Line count.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public int countLines(String path, String nodeId) throws Exception;

    /**
     * Gets the number of lines in the file.
     *
     * @param path File name.
     * @return Line count.
     * @throws Exception Exception raised by data node or no reachable nodes.
     */
    public int countLines(String path) throws Exception;

    /**
     * Appends output to the specified file. If it does not exist,
     * then it is created on a random data node.
     *
     * @param path File location.
     * @param data Data to append.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void writeFile(String path, String data) throws Exception;

    /**
     * Deletes a file from the nameserver and data node.
     *
     * @param path File location.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void deleteFile(String path) throws Exception;

    /**
     * Creates an empty file on a particular data node. Note that this only
     * registers the file on the nameserver and doesn't actually create an
     * empty file. So, it would be unwise to read before writing.
     *
     * @param path File location.
     * @param node Data node to use. hostname:port.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void createFile(String path, String node) throws Exception;

    /**
     * Determines which data nodes a files is replicated on.
     *
     * @param path Filename.
     * @return Array of hostname:port.
     * @throws Exception Invalid message received or exception raised by
     *                   name node.
     */
    public String[] getLocations(String path) throws Exception;

    /**
     * Copy a local file to the DFS.
     *
     * @param path Filename.
     * @param node Preferred data node.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFile(File path, String node) throws Exception;

    /**
     * Copy a local file to the DFS.
     *
     * @param path Filename.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFile(File path) throws Exception;

    /**
     * Copy a local file to the DFS.
     *
     * @param path Filename.
     * @param node Preferred data node.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFile(String path, String node) throws Exception;

    /**
     * Copy a local file to the DFS.
     *
     * @param path Filename.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFile(String path) throws Exception;

    /**
     * Copy a group of files to the DFS.
     *
     * @param files Files to copy.
     * @param node  Preferred data node.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFiles(File[] files, String node) throws Exception;

    /**
     * Copy a group of files to the DFS.
     *
     * @param files Files to copy.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFiles(File[] files) throws Exception;

    /**
     * Copy a group of files to the DFS.
     *
     * @param files Files to copy.
     * @param node  Preferred data node.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFiles(String[] files, String node) throws Exception;

    /**
     * Copy a group of files to the DFS.
     *
     * @param files Files to copy.
     * @throws Exception Exception raised by data node or invalid reply.
     */
    public void addLocalFiles(String[] files) throws Exception;

}
