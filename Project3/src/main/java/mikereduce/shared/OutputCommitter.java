package mikereduce.shared;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Takes output from a job and writes it to the correct file.
 */
public class OutputCommitter {

    // We assume the output location is unique for each job.
    private File outputLocation;
    private OutputStream os;

    public OutputCommitter(File loc) {
        this.outputLocation = loc;
    }


    public void commitLine(String line) {
        try {
            os.write(line.getBytes());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
