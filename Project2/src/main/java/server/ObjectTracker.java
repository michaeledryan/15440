package server;

import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class ObjectTracker {

    private ConcurrentHashMap<String, Object> objs;

    public ObjectTracker() {
        this.objs = new ConcurrentHashMap<String, Object>();
    }

    public Object lookup(String key) {
        return objs.get(key);
    }

}
