package tests;


import AFS.dataserver.DataServer;
import AFS.nameserver.NameServer;
import mikereduce.jobtracker.client.JobClient;
import mikereduce.jobtracker.server.JobTracker;
import mikereduce.worker.mapnode.MapperMain;
import org.ini4j.Ini;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Shit test.
 */
public class BaseTest {

    private static String[] args = {"-c", "test.ini"};
    private static String[] args2 = {"-c", "mapper.ini"};
    private static String[] argsName = {"-p", "9000"};
    private static String[] argsClient = {"-c", "test1Config.ini"};
    private static String[] argsData1 = {"-p", "8001", "-i", "test1"};
    private static String[] argsData2 = {"-p", "8002", "-i", "test2"};

    @Test
    public void testConnection() {

        JobTracker.main(args);
        MapperMain.main(args2);
        MapperMain.main(args2);
        MapperMain.main(args2);
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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        JobClient.main(argsClient);
    }


    public void junk() {
        Ini ini = new Ini();

        ini.put("main", "mapper", IdentityMap.class.getName());
        ini.put("main", "reducer", IdentityReducer.class.getName());
        ini.put("main", "inputFile", "test1.txt");
        ini.put("main", "outputFile", "out1.txt");
        ini.put("main", "numMappers", "4");
        ini.put("main", "numReducers", "2");
        try {
            ini.store(new File("test1Config.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
