package registry;

import remote.Remote440;
import remote.Remote440Exception;
import remote.RemoteObjectRef;

/**
 * Interface to the registry. It must support all of these operations.
 *
 * @author Michael Ryan and Alex Cappiello
 */
public interface Registry {

    /**
     * Add a new entry to the registry. Do not overwrite existing entries.
     *
     * @param name Identifier.
     * @param obj  Remote object reference.
     * @throws Remote440Exception
     */
    public void bind(String name, RemoteObjectRef ref, Remote440 obj)
            throws Remote440Exception;

    /**
     * Get all registered remote objects.
     *
     * @return A list all all registered remote objects.
     * @throws Remote440Exception
     */
    public String[] list() throws Remote440Exception;

    /**
     * Add a new entry to the registry, replacing an old one if necessary.
     *
     * @param name Identifier.
     * @param obj  Remote object reference.
     * @throws Remote440Exception
     */
    public void rebind(String name, RemoteObjectRef ref, Remote440 obj)
            throws Remote440Exception;

    /**
     * Remote an entry from the registry.
     *
     * @param name Identifier.
     * @throws Remote440Exception
     */
    public void unbind(String name) throws Remote440Exception;

    /**
     * If the given identifier exists in the registry,
     * return the remote object reference.
     *
     * @param key Identifier.
     * @return The corresponding remote object reference.
     * @throws Remote440Exception
     */
    public Remote440 lookup(String key) throws Remote440Exception;

}
