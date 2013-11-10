package mikereduce.worker.mapnode;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: michaelryan
 * Date: 11/9/13
 * Time: 7:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapperConf {

    private int port;
    private String address;

    private MapperConf(MapperConfBuilder builder) {
        port = builder.port;
        address = builder.address;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }


    public static class MapperConfBuilder {

        private int port;
        private String address;

        public MapperConfBuilder() {}


        public MapperConf buildFromFile(File location) {
            Ini ini = new Ini();
            try {
                ini.load(location);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            //Incorporate this with the file system?
            this.setPort(ini.get("main", "port", int.class));
            this.setAddress(ini.get("main", "address"));
            return this.build();
        }


        private MapperConf build() {
            return new MapperConf(this);
        }

        private void setPort(int port) {
            this.port = port;
        }

        private void setAddress(String address) {
            this.address = address;
        }

    }


}
