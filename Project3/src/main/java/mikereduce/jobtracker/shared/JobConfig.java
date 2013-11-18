package mikereduce.jobtracker.shared;

import mikereduce.shared.Mapper;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Configuration file for a single MapReduce job.
 */
public class JobConfig implements Serializable {


    // Do we call this the MikeReduce framework?
    private Class<? extends Mapper> miker;

    // Or the MapRyan framework?
    private Class<? extends Reducer> ryaner;

    private String inputPath;
    private String outputPath;
    private int numMappers = 0;
    private int numReducers = 0;

    /**
     * Parses the given File into a JobConfig.
     *
     * @param location the File to be parsed.
     */
    public JobConfig(File location) {
        Ini ini = new Ini();
        try {
            ini.load(location);
        } catch (IOException e) {
            e.printStackTrace();
        }

        miker = ini.get("main", "mapper", Class.class);
        ryaner = ini.get("main", "reducer", Class.class);
        inputPath = ini.get("main", "inputFile");
        outputPath = ini.get("main", "outputFile");
        numMappers = ini.get("main", "numMappers", int.class);
        numReducers = ini.get("main", "numReducers", int.class);


    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setMiker(Class<? extends Mapper> miker) {
        this.miker = miker;
    }

    public void setRyaner(Class<? extends Reducer> ryaner) {
        this.ryaner = ryaner;
    }

    public Class getMiker() {
        return miker;
    }

    public Class getRyaner() {
        return ryaner;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public int getNumMappers() {
        return numMappers;
    }

    public void setNumMappers(int numMappers) {
        this.numMappers = numMappers;
    }

    public int getNumReducers() {
        return numReducers;
    }

    public void setNumReducers(int numReducers) {
        this.numReducers = numReducers;
    }

}
