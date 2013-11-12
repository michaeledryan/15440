package tests;


import mikereduce.jobtracker.client.JobClient;
import mikereduce.jobtracker.server.ClientMessage;
import mikereduce.jobtracker.server.ClientMessageType;
import mikereduce.jobtracker.server.JobTracker;
import mikereduce.jobtracker.shared.JobConfig;
import mikereduce.worker.mapnode.MapperMain;
import org.junit.Before;
import org.junit.Test;

/**
 * Shit test.
 */
public class BaseTest {

    private static String[] args = {"-c", "test.ini"};
    private static String[] args2 = {"-c", "mapper.ini"};


    @Test
    public void testConnection() {

        JobTracker.main(args);
        MapperMain.main(args2);


        System.out.println("did stuff");
        JobConfig conf = new JobConfig();

        conf.setInputReader(String.class);
        conf.setMiker(String.class);
        conf.setOutputWriter(String.class);
        conf.setPartitioner(String.class);
        conf.setRyaner(String.class);
        conf.setInputPath("testIn.txt");
        conf.setOutputPath("testOut.txt");

        ClientMessage cm = new ClientMessage(ClientMessageType.NEW,conf);

        JobClient.submit(cm, "localhost", 9000);
    }

}
