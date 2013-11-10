package AFS.nameserver;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class FileMap {
    private static FileMap ourInstance = new FileMap();
    private ConcurrentHashMap<String, String> m;
    private ConcurrentHashMap<String, Socket> socketCache;
    private Random r;

    public static FileMap getInstance() {
        return ourInstance;
    }

    private FileMap() {
        m = new ConcurrentHashMap<>();
        socketCache = new ConcurrentHashMap<>();
        r = new Random();
    }

    public void put(String key, String value) {
        m.put(key, value);
    }

    public String get(String key) {
        return m.get(key);
    }

    public void batchPut(String[] keys, String value) {
        for (String key : keys) {
            m.put(key, value);
        }
    }

    public void addSocket(String host, Socket s) {
        socketCache.put(host, s);
    }

    public Socket getSocket(String host) {
        return socketCache.get(host);
    }

    public String randomHost() {
        String[] keys = socketCache.keySet().toArray(new String[0]);
        int idx = r.nextInt(keys.length);
        return keys[idx];
    }

}
