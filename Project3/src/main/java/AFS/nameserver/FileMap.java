package AFS.nameserver;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal data structures representing the organization of the file system.
 * All files and all data nodes are tracked.
 */
public class FileMap {

    private static FileMap ourInstance = new FileMap();
    private ConcurrentHashMap<String, String> m;
    private ConcurrentHashMap<String, Boolean> nodes;
    private Random r;

    public static FileMap getInstance() {
        return ourInstance;
    }

    private FileMap() {
        m = new ConcurrentHashMap<>();
        nodes = new ConcurrentHashMap<>();
        r = new Random();
    }

    /**
     * Track a new file.
     *
     * @param key Filename.
     * @param value Hostname:port of data node.
     */
    public void put(String key, String value) {
        m.put(key, value);
    }

    /**
     * Checks if a file exists.
     *
     * @param key Filename.
     * @return Whether the file key exists.
     */
    public Boolean contains(String key) {
        return m.containsKey(key);
    }

    /**
     * Get the data node that a file is stored on.
     *
     * @param key Filename.
     * @return Hostname:port of the data node that stores the file key.
     */
    public String get(String key) {
        return m.get(key);
    }

    /**
     * Remove a file from the index.
     *
     * @param key Filename.
     */
    public void delete(String key) {
        m.remove(key);
    }

    /**
     * Adds a group of files that are all stored on the same data node.
     *
     * @param keys Array of filenames.
     * @param value Hostname:port of data node storing them.
     */
    public void batchPut(String[] keys, String value) {
        System.out.println(value);
        System.out.println(Arrays.toString(keys));
        for (String key : keys) {
            m.put(key, value);
        }
    }

    /**
     * Record a new data node.
     *
     * @param host Hostname:port of the node.
     */
    public void addNode(String host) {
        nodes.put(host, true);
    }

    /**
     * Choose a random data node for new files.
     *
     * @return A valid data node.
     */
    public String randomHost() {
        String[] keys = nodes.keySet().toArray(new String[0]);
        int idx = r.nextInt(keys.length);
        return keys[idx];
    }

    /**
     * Is this a known data node?
     *
     * @param key Hostname:port.
     * @return Is it in the group of nodes?
     */
    public Boolean validHost(String key) {
        return nodes.containsKey(key);
    }

}
