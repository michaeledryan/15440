package AFS.dataserver;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class FileCache {

    private static FileCache ourInstance = new FileCache();
    private static ConcurrentHashMap<String, FileData> cache;
    private int size = 0;
    private final int MAXSIZE = 50 * 1024 * 1024;

    public static FileCache getInstance() {
        return ourInstance;
    }

    private FileCache() {
        cache = new ConcurrentHashMap<>();
    }

    private FileData add(String path) {
        FileData fd = new FileData(path);
        size += fd.getSize();

        Enumeration<String> keys = cache.keys();
        while (size > MAXSIZE && keys.hasMoreElements()) {
            this.remove(keys.nextElement());
        }

        cache.put(path, fd);

        return fd;
    }

    public String read(String path) throws IOException {
        FileData fd;
        if (cache.containsKey(path)) {
            fd = cache.get(path);
        } else {
            fd = add(path);
        }
        return fd.read();
    }

    public String readLines(String path, int start, int size)
            throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path).readLines(start, size);
        } else {
            FileData fd = add(path);
            return fd.readLines(start, size);
        }
    }

    public void write(String path, String data) throws IOException {
        FileData fd;
        if (cache.containsKey(path)) {
            fd = cache.get(path);
        } else {
            fd = add(path);
        }
        size += data.length();
        fd.write(data);
    }

    public int countLines(String path) throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path).countLines();
        } else {
            FileData fd = add(path);
            return fd.countLines();
        }
    }

    public void remove(String path) {
        if (cache.containsKey(path)) {
            size -= cache.get(path).getSize();
            cache.remove(path);
        }
    }

}
