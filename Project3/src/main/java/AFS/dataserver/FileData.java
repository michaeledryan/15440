package AFS.dataserver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Contains everything worth keeping in cache for a given file.
 * Actual disk reads are lazy (i.e. don't occur when the constructor is
 * called).
 */
public class FileData {

    private File f;
    private String contents = null;
    private String[] lines = null;
    private int len;
    private int size;

    public FileData(String path) {
        this.f = new File(path);
    }

    /**
     * File's line count.
     *
     * @return Number of lines.
     */
    private int getLineCount() {
        return contents.split("\n").length;
    }

    /**
     * Reads a file from cache, or from disk into cache if not present.
     *
     * @return File contents.
     * @throws IOException
     */
    public String read() throws IOException {
        if (contents == null) {
            contents = FileUtils.readFileToString(f, "US-ASCII");
            size = contents.length();
            len = getLineCount();
        }
        return contents;
    }

    /**
     * Reads the lines from cache, or from disk into cache if not present.
     *
     * @param start First line to read.
     * @param size  Number of lines to read.
     * @return Contents of the specified lines.
     * @throws IOException
     */
    public String readLines(int start, int size) throws IOException {
        if (lines == null) {
            if (contents == null) {
                this.read();
            }
            lines = contents.split("\n");
            len = lines.length;
        }
        String contents = "";
        int limit = start + size;
        Boolean abort = false;

        for (int i = start; i < limit; i++) {
            if (i >= lines.length) {
                abort = true;
                break;
            }
            contents += lines[i];
            if (i != limit - 1) {
                contents += "\n";
            }
        }

        if (abort) {
            throw new IOException("Lines out of range.");
        }
        return contents;
    }

    /**
     * Appends new data to the file.
     * This is both updated in the cache and write-through to disk.
     *
     * @param data Data to append.
     * @throws IOException
     */
    public void write(String data) throws IOException {
        if (contents == null && f.exists()) {
            this.readLines(0, 0);
        } else if (contents == null) {
            contents = "";
            lines = new String[0];
        }
        contents += data;
        String[] ln = data.split("\n");
        len += ln.length;
        size += data.length();
        File dir = new File(f.getParent());
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create parent " +
                        "directories.");
            }
        }
        lines = (String[]) ArrayUtils.addAll(lines, ln);
        FileOutputStream w = new FileOutputStream(f, true);
        w.write(data.getBytes());
        w.close();
    }

    /**
     * Get the number of lines in the file.
     *
     * @return Line count.
     * @throws IOException
     */
    int countLines() throws IOException {
        if (contents == null) {
            this.read();
        }
        return len;
    }

    /**
     * Get the number of bytes in the file.
     *
     * @return Byte count.
     */
    public int getSize() {
        return size;
    }

}
