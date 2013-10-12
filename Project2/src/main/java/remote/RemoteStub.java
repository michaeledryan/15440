package remote;

import java.io.Serializable;

/**
 * Interface that marks the stubs for remote classes.
 * 
 * @author Michael Ryan and Alex Cappiello
 * 
 */
public interface RemoteStub extends Serializable {

	/**
	 * Used to set the RemoteObjectReference when a stub is localized.
	 * 
	 * @param ror
	 *            the RemoteObjectReference being localized.
	 */
	public void setRemoteRef(RemoteObjectRef ror);
}
