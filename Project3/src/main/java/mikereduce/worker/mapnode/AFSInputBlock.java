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

    @Override
    public String getLine() {
        if (conn == null) {
            setupBlock();
        }

        return block.get(blockIndex - 1);
    }

    @Override
    public int getLines() {
        if (conn == null) {
            setupBlock();
        }
        return block.size();
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

    /**
     * Loads the block this node is working on from the DFS into memory. Streams data in chunks to avoid
     * requests for remote files taking too long.
     */
    private void setupBlock() {
        conn = new Connection(hostname, port);
        StringBuilder builder = new StringBuilder();
        for (int j = offset; j < size + offset; j += size / 10) {
            try {
                String n = conn.readLines(filePath, j, Math.min(offset + size - j, size / 10));
                builder.append(n + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Convert from StringBuilder to block.
        block = new ArrayList<>(Arrays.asList(builder.toString().split("\n")));
    }
}
