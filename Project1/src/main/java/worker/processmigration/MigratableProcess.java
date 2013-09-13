package worker.processmigration;

import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable {

	void suspend();
	
	public int getClientID();
	
	public int getProcessID();

	void setProcessID(int id);
	
}
