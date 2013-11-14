package tests;


import AFS.dataserver.DataServer;
import AFS.nameserver.NameServer;
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
    private static String[] argsName = {"-p", "9000"};
    private static String[] argsData1 = {"-p", "8001", "-i", "test1"};
    private static String[] argsData2 = {"-p", "8002", "-i", "test2"};

    @Test
    public void testConnection() {

        JobTracker.main(args);
        MapperMain.main(args2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                NameServer.main(argsName);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataServer.main(argsData1);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataServer.main(argsData2);
            }
        }).start();

        try {
            Thread.sleep(4000);
            System.out.println("done with sleep.");
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        JobConfig conf = new JobConfig();

        conf.setInputReader(String.class);
        conf.setMiker(IdentityMap.class);
        conf.setOutputWriter(String.class);
        conf.setPartitioner(String.class);
        conf.setRyaner(String.class);
        conf.setInputPath("a.txt");
        conf.setOutputPath("testOut.txt");

        ClientMessage cm = new ClientMessage(ClientMessageType.NEW,conf);

        System.out.println("sending message...");
        JobClient.submit(cm, "localhost", 9001);
    }

}
