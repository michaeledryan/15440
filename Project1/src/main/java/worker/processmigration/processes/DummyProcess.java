package worker.processmigration.processes;

import java.util.Random;

import worker.processmigration.AbstractMigratableProcess;

public class DummyProcess extends AbstractMigratableProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8647456055805832810L;
	private boolean suspended = false;
	private int limit;
	
	@Override
	public void suspend() {
		
	}

	public DummyProcess(String[] args) throws Exception {
		if (args.length != 1) {
			System.out
			.println("usage: DummyProcess <limit>");
			throw new Exception("Invalid Arguments");
		}
		limit = Integer.parseInt(args[0]);
	}
	
	
	@Override
	public void run() {
		Random rand = new Random();
		int i = 0;
		while (i++ < 10 && !suspended) {
			System.out.println(rand.nextInt(limit));
			try {
				Thread.sleep(1000 + rand.nextInt() % 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	
}
