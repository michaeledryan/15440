package worker.processmigration;

import java.io.Serializable;

/**
 * Interface for migratable processes. Objects that implement this interface can
 * be run via this framework on any worker node that shares access to a
 * filesystem.
 * 
 * @author michaelryan
 * 
 */
public interface MigratableProcess extends Runnable, Serializable {

	/**
	 * Suspends operation of the process. Usually sets a suspend flag to false,
	 * which causes the process to save its state and close any open file
	 * descriptors or sockets.
	 */
	void suspend();

	/**
	 * Resumes a suspended process. Reinitializes any state that might have to
	 * be reconstituted after suspension, serialization, and deserialization.
	 */
	void restart();

	/* See abstract class AbstractMigratableProcess */

	/**
	 * @return the ID of the client that spawned this process.
	 */
	public int getClientID();

	/**
	 * @deprecated Sets the clientID of this process. Tampering with this may
	 *             disrupt framework operation.
	 * @param clientID
	 */
	void setClientID(int clientID);

	/**
	 * @return The pid of this process.
	 */
	public int getProcessID();

	/**
	 * @deprecated Sets the pid of this process. Tampering with this may disrupt
	 *             framework operation.
	 * @param id
	 */
	void setProcessID(int id);

}
