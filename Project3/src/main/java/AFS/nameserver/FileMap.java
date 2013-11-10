package AFS.nameserver;

import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class FileMap {
    private static FileMap ourInstance = new FileMap();
    private ConcurrentHashMap<String, String> m;

    public static FileMap getInstance() {
        return ourInstance;
    }

    private FileMap() {
        m = new ConcurrentHashMap<>();
    }

    public void put(String key, String value) {
        m.put(key, value);
    }

    public String get(String key) {
        return m.get(key);
    }

}
