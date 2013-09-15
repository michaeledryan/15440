package worker.processmigration;

import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable {

	void suspend();
	
	void restart();
	
	public int getClientID();
	
	void setClientID(int clientID);
	
	public int getProcessID();

	void setProcessID(int id);
	
}
