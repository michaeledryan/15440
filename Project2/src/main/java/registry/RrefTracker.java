package registry;

import remote.Remote440;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class RrefTracker implements Registry {

    private ConcurrentHashMap<String, Remote440> refs;

    public RrefTracker() {
        this.refs = new ConcurrentHashMap<>();
    }

    public Remote440 lookup(String key) {
        return refs.get(key);
    }

    private void put(String key, Remote440 ref) {
        refs.put(key, ref);
    }

    @Override
    public void bind(String name, Remote440 obj) throws RemoteException {
        if (refs.containsKey(name)) {
            throw new RemoteException("Item already in registry: " + name);
        }
        this.put(name, obj);
    }

    @Override
    public void rebind(String name, Remote440 obj) throws RemoteException {
        this.put(name, obj);
    }

    @Override
    public void unbind(String name) throws RemoteException {
        refs.remove(name);
    }

    @Override
    public Set<String> list() throws RemoteException {
        return refs.keySet();
    }
}
