package processmanagement;

import worker.processmigration.MigratableProcess;

public interface ProcessRunner {

	void registerProcess(MigratableProcess process);

}
