package AFS.nameserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal data structures representing the organization of the file system.
 * All files and all data nodes are tracked.
 */
public class FileMap {

    private static FileMap ourInstance = new FileMap();
    private ConcurrentHashMap<String, ArrayList<String>> m;
    private ConcurrentHashMap<String, String> nodes;
    private int replication = 2;
    private Random r;

    public static FileMap getInstance() {
        return ourInstance;
    }

    private FileMap() {
        m = new ConcurrentHashMap<>();
        nodes = new ConcurrentHashMap<>();
        r = new Random();
    }

    public String flattenHosts(ArrayList<String> hosts) {
        String res = "";
        for (int i = 0; i < hosts.size(); i++) {
            res += hosts.get(i);
            if (i != hosts.size() - 1) {
                res += ";";
            }
        }
        return res;
    }

    /**
     * Track a new file.
     *
     * @param key Filename.
     * @param value Hostname:port of data node.
     */
    public void put(String key, String value) {
        if (m.containsKey(key)) {
            m.get(key).add(value);
        } else {
            ArrayList<String> data = new ArrayList<>(replication);
            data.add(value);
            m.put(key, data);
        }
    }

    public void putAll(String key, ArrayList<String> value) {
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
        return flattenHosts(m.get(key));
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
            put(key, value);
        }
    }

    /**
     * Record a new data node.
     *
     * @param host Hostname:port of the node.
     */
    public void addNode(String id, String host) {
        nodes.put(id, host);
    }

    public String getNode(String id) {
        return nodes.get(id);
    }

    private void shuffle(ArrayList<String> arr) {
        for (int i = arr.size()-1; i >= 1; i--) {
            int idx = r.nextInt(i);
            String tmp = arr.get(i);
            arr.set(i, arr.get(idx));
            arr.set(idx, tmp);
        }
    }

    /**
     * Choose a random data node for new files.
     *
     * @return A valid data node.
     */
    public ArrayList<String> randomHosts(int n) {
        ArrayList<String> hosts = new ArrayList<>(nodes.values());
        shuffle(hosts);
        return hosts;
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

    public int getReplication() {
        return replication;
    }

    public void setReplication(int replication) {
        this.replication = replication;
    }
}
