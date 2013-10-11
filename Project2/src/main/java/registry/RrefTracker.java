package registry;

import remote.MyRemote;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class RrefTracker implements Registry {

    private ConcurrentHashMap<String, MyRemote> refs;

    public RrefTracker() {
        this.refs = new ConcurrentHashMap<>();
    }

    public MyRemote lookup(String key) {
        return refs.get(key);
    }

    private void put(String key, MyRemote ref) {
        refs.put(key, ref);
    }

    @Override
    public void bind(String name, MyRemote obj) throws RemoteException {
        if (refs.containsKey(name)) {
            throw new RemoteException("Item already in registry: " + name);
        }
        this.put(name, obj);
    }

    @Override
    public void rebind(String name, MyRemote obj) throws RemoteException {
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
