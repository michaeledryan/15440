package AFS.dataserver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 */
public class FileData {

    private File f;
    private String contents = null;
    private String[] lines = null;
    private int len;

    public FileData(String path) {
        this.f = new File(path);
    }

    public FileData(File f) {
        this.f = f;
    }

    private int getLineCount() {
        return contents.split("\n").length;
    }

    public String read() throws IOException {
        if (contents == null) {
            contents = FileUtils.readFileToString(f, "US-ASCII");
            len = getLineCount();
        }
        return contents;
    }

    public String readLines(int start, int size) throws IOException {
        if (lines == null) {
            if (contents == null) {
                this.read();
            }
            System.out.println(contents);
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

    public void write(String data) throws IOException {
        if (contents == null && f.exists()) {
            this.readLines(0,0);
        } else if (contents == null) {
            contents = "";
            lines = new String[0];
        }
        contents += data;
        String[] ln = data.split("\n");
        len += ln.length;
        File dir = new File(f.getParent());
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create parent " +
                                "directories.");
            }
        }
        lines = (String[])ArrayUtils.addAll(lines, ln);
        FileOutputStream w = new FileOutputStream(f, true);
        w.write(data.getBytes());
        w.close();
    }

    int countLines() throws IOException {
        if (contents == null) {
            this.read();
        }
        return len;
    }

}
