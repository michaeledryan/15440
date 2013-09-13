package worker.processmanagement;

import worker.processmigration.MigratableProcess;

public class ProcessThread implements Runnable{
	
	private MigratableProcess process;
	private ProcessRunner runner;
	private ProcessState ps;
	
	
	public ProcessThread(MigratableProcess process, ProcessRunner runner) {
		this.process = process;
		this.runner = runner;
		this.ps = ProcessState.RUNNING;
	}


	@Override
	public void run() {
		process.run();
		System.out.println("SHIT'S DONE!");
		runner.ackDone(process);
	}
	
	
	public void suspend() {
		process.suspend();
	}
	
	public ProcessState getProcessState() {
		return ps;
	}

	public MigratableProcess getProcess() {
		return process;
	}

	public void unSuspend() {
		// TODO Auto-generated method stub
		
	}

}
