package server;

import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class ObjectTracker {

    private ConcurrentHashMap<String, Object> objs;

    public ObjectTracker() {
        this.objs = new ConcurrentHashMap<>();
    }

    public Object lookup(String key) {
        return objs.get(key);
    }

    public void put(String key, Object obj) {
    	objs.put(key, obj);
    }

}
