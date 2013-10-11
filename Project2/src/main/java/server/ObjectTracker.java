package server;

import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class ObjectTracker {

    private ConcurrentHashMap<String, Object> objs;
    private static ObjectTracker instance = null;

    private ObjectTracker() {
        this.objs = new ConcurrentHashMap<String, Object>();
    }

    public static ObjectTracker getInstance() {
        if (instance == null) {
            instance = new ObjectTracker();
        }
        return instance;
    }

    public Object lookup(String key) {
        return objs.get(key);
    }

    public Object put(String key, Object obj) {
        return objs.put(key, obj);
    }

}
