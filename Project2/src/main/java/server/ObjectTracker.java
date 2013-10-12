package server;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps object identifiers to the local objects.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public class ObjectTracker {

    private ConcurrentHashMap<String, Object> objs;
    private static ObjectTracker instance = null;

    private ObjectTracker() {
        this.objs = new ConcurrentHashMap<>();
    }

    public static ObjectTracker getInstance() {
        if (instance == null) {
            instance = new ObjectTracker();
        }
        return instance;
    }

    /**
     * Do a lookup in the hash map. Exciting.
     *
     * @param key Object identifier.
     * @return Reference to the object.
     */
    public Object lookup(String key) {
        return objs.get(key);
    }

    /**
     * Add a new object to the map.
     *
     * @param key Object identifier.
     * @param obj Unspecified object.
     */
    public void put(String key, Object obj) {
        objs.put(key, obj);
    }

    /**
     * Delete an object from the local map.
     *
     * @param key Object identifier.
     * @return The deleted object.
     */
    public Object delete(String key) {
        return objs.remove(key);
    }

}
