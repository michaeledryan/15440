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
	private int time;
	
	@Override
	public void suspend() {
		suspended = true;
	}

	public DummyProcess(String[] args) throws Exception {
		if (args.length != 2) {
			System.out
			.println("usage: DummyProcess <limit> <time>");
			throw new Exception("Invalid Arguments");
		}
		limit = Integer.parseInt(args[0]);
		time = Integer.parseInt(args[1]);
	}
	
	
	@Override
	public void run() {
		Random rand = new Random();
		int i = 0;
		while (i++ < time && !suspended) {
			System.out.println(rand.nextInt(limit));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	@Override
	public void restart() {
		suspended = false;
	}

	
}
