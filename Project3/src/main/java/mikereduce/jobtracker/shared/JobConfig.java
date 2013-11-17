package mikereduce.jobtracker.shared;

import mikereduce.shared.Mapper;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobConfig implements Serializable {

    private Class inputReader;
    private Class<? extends Mapper> miker;
    private Class<? extends Partitioner> partitioner;
    private Class<? extends Reducer> ryaner;
    private Class outputWriter;
    private String inputPath;
    private String outputPath;
    private int numMappers = 0;
    private int numReducers = 0;

    public JobConfig() {

    }



    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setInputReader(Class inputReader) {
        this.inputReader = inputReader;
    }

    public void setMiker(Class miker) {
        this.miker = miker;
    }

    public void setPartitioner(Class partitioner) {
        this.partitioner = partitioner;
    }

    public void setRyaner(Class ryaner) {
        this.ryaner = ryaner;
    }

    public void setOutputWriter(Class outputWriter) {
        this.outputWriter = outputWriter;
    }

    public Class getMiker() {
        return miker;
    }

    public Class getPartitioner() {
        return partitioner;
    }

    public Class getRyaner() {
        return ryaner;
    }

    public Class getOutputWriter() {
        return outputWriter;
    }

    public Class getInputReader() {
        return inputReader;
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
