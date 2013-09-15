package worker.processmigration.processes;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import worker.processmigration.AbstractMigratableProcess;
import worker.processmigration.io.TransactionalFileInputStream;
import worker.processmigration.io.TransactionalFileOutputStream;

public class InterleaveLineProcess extends AbstractMigratableProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1343750879345L;
	private TransactionalFileInputStream input1;
	private TransactionalFileInputStream input2;
	private TransactionalFileOutputStream output;

	private boolean suspended = false;

	/**
	 * Migratable process that takes two files and interleaves them.
	 * 
	 * @param args
	 *            expects three arguments: two input files and one output file.
	 */
	public InterleaveLineProcess(String[] args) throws IllegalArgumentException {
		if (args.length != 3) {
			throw new IllegalArgumentException(
					"Needs three arguments: Two input filenames followed by one output filename.");
		}

		try {
			input1 = new TransactionalFileInputStream(args[0]);
			input2 = new TransactionalFileInputStream(args[1]);
			output = new TransactionalFileOutputStream(args[2]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void suspend() {
		suspended = true;
	}

	@Override
	public void restart() {
		suspended = false;
	}

	@Override
	public void run() {

		String l1 = null, l2 = null;
		
		DataInputStream i1Reader = new DataInputStream(input1);
		DataInputStream i2Reader = new DataInputStream(input2);
		
		boolean finished = false;
		while (!suspended && !finished) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			try {
				l1 = i1Reader.readLine();
				l2 = i2Reader.readLine();
				
				if (l1 != null) {
					output.write((l1 + "\n").getBytes());
				} if (l2 != null) {
					output.write((l2 + "\n").getBytes());
				} 
				
				if (l1 == null && l2 == null) {
					finished = true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		// TODO Auto-generated method stub

	}
}
