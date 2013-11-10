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
    private Class<Mapper> miker;
    private Class partitioner;
    private Class comparator;
    private Class ryaner;
    private Class outputWriter;

    public JobConfig() {

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

    public void setComparator(Class comparator) {
        this.comparator = comparator;
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

    public Class getComparator() {
        return comparator;
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

}
