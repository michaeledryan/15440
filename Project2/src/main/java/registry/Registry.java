package registry;

import java.util.Set;

import remote.Remote440;
import remote.Remote440Exception;

import java.util.Set;

public interface Registry {

	public void bind(String name, Remote440 obj) throws Remote440Exception;

	public String[] list() throws Remote440Exception;

	public void rebind(String name, Remote440 obj) throws Remote440Exception;

	public void unbind(String name) throws Remote440Exception;

    public Remote440 lookup(String key) throws Remote440Exception;

}
