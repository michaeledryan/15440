package mikereduce.worker.mapnode;

import AFS.Connection;
import mikereduce.shared.InputBlock;

import java.util.*;

/**
 */
public class AFSReduceInputBlock implements InputBlock {

    private final String hostname;
    private final int port;
    private Set<String> filePaths;
    private Connection conn;
    private List<String> block;
    private int blockIndex = 1;

    public AFSReduceInputBlock(Set<String> filePaths, String hostname, int port) {
        this.filePaths = filePaths;
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
        System.out.println("mothafuckin reduce!: " + filePaths.toString());
        conn = new Connection(hostname, port);
        block = new ArrayList<>();

        try {
            for (String filename : filePaths) {
                String blockStr = conn.readFile(filename);

                List<String> oneBlock = new ArrayList<>(Arrays.asList(blockStr.split("\n")));
                if (!oneBlock.isEmpty() && !blockStr.equals("")) {
                    block.addAll(oneBlock);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(block);
    }
}
