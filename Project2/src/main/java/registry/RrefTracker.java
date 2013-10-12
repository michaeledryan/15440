package registry;

import remote.Remote440;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The registry maps identifiers to remote object references.
 */
public class RrefTracker implements Registry {

    private ConcurrentHashMap<String, Remote440> refs;
    private static RrefTracker instance = null;

    public RrefTracker() {
        this.refs = new ConcurrentHashMap<>();
    }

    public RrefTracker getInstance() {
        if (instance == null) {
            instance = new RrefTracker();
        }
        return instance;
    }

    /**
     * Lookup a remote object.
     */
    public Remote440 lookup(String key) {
        return refs.get(key);
    }

    /**
     * Internal add new object.
     * @param key Identifier.
     * @param ref Remote object reference.
     */
    private void put(String key, Remote440 ref) {
        refs.put(key, ref);
    }

    /**
     * Add a new Object. Does not accept rebinds.
     * @param name Identifier.
     * @param obj Remote object reference.
     * @throws RemoteException
     */
    @Override
    public void bind(String name, Remote440 obj) throws RemoteException {
        if (refs.containsKey(name)) {
            throw new RemoteException("Item already in registry: " + name);
        }
        this.put(name, obj);
    }

    /**
     * Add a new Object, possibly overwriting an old one.
     * @param name Identifier.
     * @param obj Remote object reference.
     * @throws RemoteException
     */
    @Override
    public void rebind(String name, Remote440 obj) throws RemoteException {
        this.put(name, obj);
    }

    /**
     * Unregister an Object.
     * @param name Identifier.
     * @throws RemoteException
     */
    @Override
    public void unbind(String name) throws RemoteException {
        if (!refs.containsKey(name)) {
            throw new RemoteException("Object not in registry.");
        }
        refs.remove(name);
    }

    /**
     * Retrieve a Set of everything in the registry.
     * @return Set of keys in the registry.
     * @throws RemoteException
     */
    @Override
    public Set<String> list() throws RemoteException {
        return refs.keySet();
    }
}
