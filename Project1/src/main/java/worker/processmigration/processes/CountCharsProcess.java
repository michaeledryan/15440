package worker.processmigration.processes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import worker.processmigration.AbstractMigratableProcess;
import worker.processmigration.io.TransactionalFileInputStream;
import worker.processmigration.io.TransactionalFileOutputStream;

public class CountCharsProcess extends AbstractMigratableProcess {

	private static final long serialVersionUID = -7135866757337110673L;
	
	private TransactionalFileInputStream input;
	private TransactionalFileOutputStream output;
	private Boolean suspended = false;
	int[] ascii;
	int idx;

	public CountCharsProcess(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException(
					"Needs three arguments: Two input filenames followed by one output filename.");
		}

		try {
			input = new TransactionalFileInputStream(args[0]);
			output = new TransactionalFileOutputStream(args[1]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ascii = new int[128];
		Arrays.fill(ascii, 0);
		idx = 0;
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
		Boolean finished1 = false;
		while (!suspended && !finished1) {
			try {
				int c = input.read();
				if (c == -1) {
					finished1 = true;
				}
				else {
					ascii[c]++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Boolean finished2 = false;
		while (idx < ascii.length && finished1 && !finished2) {
			try {
				if (idx == 0) {
					output.write("ascii\t\tcount\n".getBytes());
				}
				String line = String.format("%d:\t\t%d\n", idx, ascii[idx]);
				output.write(line.getBytes());
				idx++;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}

}
