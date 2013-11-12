package AFS.nameserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
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

    public void put(String key, String value) {
        m.put(key, value);
    }

    public Boolean contains(String key) {
        return m.containsKey(key);
    }

    public String get(String key) {
        System.out.println(m.toString());
        return m.get(key);
    }

    public void delete(String key) {
        m.remove(key);
    }

    public void batchPut(String[] keys, String value) {
        System.out.println(value);
        System.out.println(Arrays.toString(keys));
        for (String key : keys) {
            m.put(key, value);
        }
    }

    public void addNode(String host) {
        nodes.put(host, true);
    }

    public String randomHost() {
        String[] keys = nodes.keySet().toArray(new String[0]);
        int idx = r.nextInt(keys.length);
        return keys[idx];
    }

    public Boolean validHost(String key) {
        return nodes.containsKey(key);
    }

}
