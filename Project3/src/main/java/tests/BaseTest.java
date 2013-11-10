package tests;


import mikereduce.jobtracker.client.JobClient;
import mikereduce.jobtracker.server.JobTracker;
import mikereduce.jobtracker.shared.JobConfig;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseTest {

    private static String[] args = {"-c", "test.ini"};

    @Before
    public void startTracker() {
        JobTracker.main(args);
    }


    @Test
    public void testConnection() {
        JobConfig conf = new JobConfig();

        conf.setComparator(String.class);
        conf.setInputReader(String.class);
        conf.setMiker(String.class);
        conf.setOutputWriter(String.class);
        conf.setPartitioner(String.class);
        conf.setRyaner(String.class);


        JobClient.submit(conf, "localhost", 9000);
    }

}
