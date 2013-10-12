package registry;

import remote.Remote440Exception;
import remote.RemoteObjectRef;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The registry maps identifiers to remote object references.
 */
public class RrefTracker implements Registry {

    private ConcurrentHashMap<String, RemoteObjectRef> refs;
    private static RrefTracker instance = null;

    public RrefTracker() {
        this.refs = new ConcurrentHashMap<>();
    }

    public static RrefTracker getInstance() {
        if (instance == null) {
            instance = new RrefTracker();
        }
        return instance;
    }

    /**
     * Lookup a remote object.
     */
    public RemoteObjectRef lookup(String key) {
        return refs.get(key);
    }

    /**
     * Internal add new object.
     * @param key Identifier.
     * @param ref Remote object reference.
     */
    private void put(String key, RemoteObjectRef ref) {
        refs.put(key, ref);
    }

    /**
     * Add a new Object. Does not accept rebinds.
     * @param name Identifier.
     * @param obj Remote object reference.
     * @throws Remote440Exception
     */
    @Override
    public void bind(String name, RemoteObjectRef obj) throws Remote440Exception {
        if (refs.containsKey(name)) {
            throw new Remote440Exception("Item already in registry: " + name);
        }
        this.put(name, obj);
    }

    /**
     * Add a new Object, possibly overwriting an old one.
     * @param name Identifier.
     * @param obj Remote object reference.
     * @throws Remote440Exception
     */
    @Override
    public void rebind(String name, RemoteObjectRef obj) throws Remote440Exception {
        this.put(name, obj);
    }

    /**
     * Unregister an Object.
     * @param name Identifier.
     * @throws Remote440Exception
     */
    @Override
    public void unbind(String name) throws Remote440Exception {
        if (!refs.containsKey(name)) {
            throw new Remote440Exception("Object not in registry.");
        }
        refs.remove(name);
    }

    /**
     * Retrieve a Set of everything in the registry.
     * @return Set of keys in the registry.
     * @throws Remote440Exception
     */
    @Override
    public String[] list() throws Remote440Exception {
        Object[] keys = refs.keySet().toArray();
        String[] stringArray = Arrays.copyOf(keys, keys.length, String[].class);

        return stringArray;
    }
}
