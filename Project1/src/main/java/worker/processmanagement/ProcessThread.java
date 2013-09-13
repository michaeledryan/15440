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
	public ProcessThread(MigratableProcess process, ProcessRunner runner) {
		this.process = process;
		this.runner = runner;
		this.ps = ProcessState.RUNNING;
	}

	/**
	 * Continue a process that has been serialized.
	 * 
	 * @param serializedPath
	 *            File to serialized output.
	 * @param runner
	 *            Worker running this process.
	 * @throws Exception
	 */
	public ProcessThread(File serializedPath, ProcessRunner runner)
			throws Exception {
		this.runner = runner;
		this.restore(serializedPath);
	}

	@Override
	public void run() {
		process.run();
		System.out.println("SHIT'S DONE!");

		// Test Serialization/Deserialization for now. Remove once actually
		// implemented.
		try {
			File save = this.suspend();
			new ProcessThread(save, this.runner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runner.ackDone(process);
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

	public void unSuspend() {
	}

}
