package AFS.dataserver;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class FileCache {

    private static FileCache ourInstance = new FileCache();
    private static ConcurrentHashMap<String, FileData> cache;

    public static FileCache getInstance() {
        return ourInstance;
    }

    private FileCache() {
        cache = new ConcurrentHashMap<>();
    }

    public String read(String path) throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path).read();
        } else {
            FileData fd = new FileData(path);
            cache.put(path, fd);
            return fd.read();
        }
    }

    public String readLines(String path, int start, int size)
            throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path).readLines(start, size);
        } else {
            FileData fd = new FileData(path);
            cache.put(path, fd);
            return fd.readLines(start, size);
        }
    }

    public void write(String path, String data) throws IOException {
        if (cache.containsKey(path)) {
            cache.get(path).write(data);
        } else {
            FileData fd = new FileData(path);
            cache.put(path, fd);
            fd.write(data);
        }
    }

    public int countLines(String path) throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path).countLines();
        } else {
            FileData fd = new FileData(path);
            cache.put(path, fd);
            return fd.countLines();
        }
    }

    public void remove(String path) {
        cache.remove(path);
    }

}
