package worker.processmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

import worker.processmigration.MigratableProcess;

import common.Util;

/**
 * Wraps a MigratableProcess to simplify the interface.
 * 
 * @author michaelryan
 */
public class ProcessThread implements Runnable {

	private MigratableProcess process;
	private ProcessRunner runner;
	private ProcessState ps;

	/**
	 * Start a new process on the worker.
	 * 
	 * @param process
	 *            MigratableProcess instance.
	 * @param runner
	 *            Worker running this process.
	 */
	public ProcessThread(MigratableProcess process) {
		this.process = process;
		this.runner = ProcessRunner.getInstance();
		this.ps = ProcessState.RUNNING;
	}

	/**
	 * Continue a process that has been serialized.
	 * 
	 * @param serializedPath
	 *            File to serialized output.
	 * @throws Exception
	 */
	public ProcessThread(File serializedPath) throws Exception {
		this.runner = ProcessRunner.getInstance();
		this.restore(serializedPath);
	}

	/**
	 * Run message. Runs the process and reports back to the master when done.
	 */
	@Override
	public void run() {
		process.run();
		System.out.println("SHIT'S DONE!");
		if (ps == ProcessState.RUNNING){
			runner.ackDone(process);
		}
	}

	/**
	 * Suspend process and write out to a file.
	 * 
	 * @return File that was saved.
	 * @throws IOException
	 */
	// Might be easier to return a String.
	public File suspend() throws IOException {
		// Suspend process.
		this.process.suspend();
		this.ps = ProcessState.SUSPENDED;

		// Ensure each worker has a unique directory to save to.
		String hostname = InetAddress.getLocalHost().getHostName();
		int port = this.runner.getPort();
		File dir = new File("scratch/" + hostname + ":"
				+ Integer.toString(port));
		dir.mkdirs();

		// Use MD5 to choose a filename. This *should* be unique.
		String md5 = Util.MD5(process.toString());
		File savefile = new File(dir, md5 + ".bin");
		System.out.printf("Serializing to %s\n", savefile.getAbsolutePath());

		// Write the file.
		FileOutputStream outfile = new FileOutputStream(savefile);
		ObjectOutputStream oos = new ObjectOutputStream(outfile);
		oos.writeObject(this.process);
		oos.close();
		outfile.close();

		return savefile;
	}

	public ProcessState getProcessState() {
		return ps;
	}

	public MigratableProcess getProcess() {
		return process;
	}

	/**
	 * Restore a saved process from file. Afterwards, the file is deleted.
	 * 
	 * @param savefile
	 *            File location.
	 * @throws Exception
	 */
	public void restore(File savefile) throws Exception {
		System.out
				.printf("Deserializing from %s\n", savefile.getAbsolutePath());

		
		// Read the file.
		FileInputStream infile = new FileInputStream(savefile);
		ObjectInputStream ois = new ObjectInputStream(infile);
		Object obj = ois.readObject();
		ois.close();
		infile.close();

		// Ensure we have a valid object.
		if (obj instanceof MigratableProcess) {
			this.process = (MigratableProcess) obj;
			this.ps = ProcessState.RUNNING;
			savefile.delete();
		} else {
			throw new Exception("Unable to deserialize process: "
					+ savefile.getAbsolutePath());
		}
	}

	public void restart() {
		process.restart();
	}

}
