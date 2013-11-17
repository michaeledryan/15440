package mikereduce.worker.mapnode;

import AFS.Connection;
import mikereduce.shared.InputBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * InputBlock that reads from AFS.
 */
public class AFSInputBlock implements InputBlock {

    private final String hostname;
    private final int port;
    private String filePath;
    private int offset;
    private int size;
    private Connection conn;
    private List<String> block;
    private int blockIndex = 1;

    public AFSInputBlock(String filePath, int startLine, int endLine, String hostname, int port) {
        this.filePath = filePath;
        this.offset = startLine;
        this.size = endLine;
        this.hostname = hostname;
        this.port = port;
    }


    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }


    @Override
    public String getLine() {
        if (conn == null) {

            setupBlock();
        }

        return block.get(blockIndex - 1);
    }

    @Override
    public boolean nextLine() {
        if (conn == null) {
            setupBlock();

            return block.size() != 0;

        } else {
            if (blockIndex++ == block.size()) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public boolean checkNext() {
        return (block == null) || blockIndex <= block.size();
    }

    private void setupBlock() {
        System.out.println("start at line: " + offset + ", end at line: " + (offset + size));
        conn = new Connection("localhost", 9000); // TODO: Add actual location.
        try {
            block = new ArrayList<>(Arrays.asList(conn.readLines(filePath, offset, size).split("\n")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
