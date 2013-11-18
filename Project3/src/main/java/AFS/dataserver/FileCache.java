package AFS.dataserver;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache files to cut down disk IO.
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

    /**
     * Add a new file to the cache, evicting if necessary.
     *
     * @param path Filename.
     * @return Cached file data.
     */
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

    /**
     * Gets file contents.
     *
     * @param path
     * @return
     * @throws IOException
     */
    public String read(String path) throws IOException {
        FileData fd;
        if (cache.containsKey(path)) {
            fd = cache.get(path);
        } else {
            fd = add(path);
        }
        return fd.read();
    }

    /**
     * Reads the specified lines.
     *
     * @param path  Filename.
     * @param start First line to read.
     * @param size  Number of lines to read.
     * @return Contents.
     * @throws IOException
     */
    public String readLines(String path, int start, int size)
            throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path).readLines(start, size);
        } else {
            FileData fd = add(path);
            return fd.readLines(start, size);
        }
    }

    /**
     * Writes data to the file.
     *
     * @param path Filename.
     * @param data Data to append.
     * @throws IOException
     */
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

    /**
     * Gets the number of lines in the file.
     *
     * @param path Filename.
     * @return Line count.
     * @throws IOException
     */
    public int countLines(String path) throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path).countLines();
        } else {
            FileData fd = add(path);
            return fd.countLines();
        }
    }

    /**
     * Remove an entry from the cache.
     *
     * @param path Filename.
     */
    public void remove(String path) {
        if (cache.containsKey(path)) {
            size -= cache.get(path).getSize();
            cache.remove(path);
        }
    }

}
