package mikereduce.worker.mapnode;

import AFS.Connection;
import mikereduce.shared.InputBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * InputBlock that reads from AFS.
 */
public class AFSInputBlock implements InputBlock {

    private String filePath;
    private int offset;
    private int size;
    private Connection conn;
    private List<String> block;
    private int blockIndex = 0;

    public AFSInputBlock(String filePath, int offset, int size) {
        this.filePath = filePath;
        this.offset = offset;
        this.size = size;
    }


    public String getFilePath() {
        return filePath;
    }

    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }

    @Override
    public long getLength() {
        return size;
    }

    @Override
    public String getLine() {
        return block.get(blockIndex++);
    }

    @Override
    public boolean nextLine() {
        if (conn == null) {

            conn = new Connection("localhost", 9000); // TBD
            try {
                block = new ArrayList<String>(Arrays.asList(conn.readFile(filePath).split("\n")));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return block.size() != 0;

        } else {
            if (blockIndex == block.size()) {
                return false;
            } else {
                return true;
            }

        }

    }
}
