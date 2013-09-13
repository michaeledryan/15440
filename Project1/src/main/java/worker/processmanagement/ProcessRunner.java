package worker.processmanagement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import worker.processmigration.MigratableProcess;

public class ProcessRunner implements Runnable {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;

	private int port;

	private Map<Integer, ProcessThread> idsToProcesses = new ConcurrentHashMap<Integer, ProcessThread>();

	public ProcessRunner(int port) {
		this.port = port;
	}
	
	public int getPort () {
		return this.port;
	}

	@Override
	public void run() {

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) {
			try {
				clientSocket = serverSocket.accept();
				this.outStream = new ObjectOutputStream(
						this.clientSocket.getOutputStream());
				this.inStream = new ObjectInputStream(
						this.clientSocket.getInputStream());

				// TODO: Establish protocol for sending data back to Master

				Object obj;
				while (true) {
					obj = this.inStream.readObject();
					if (obj == null) {
						break;
					}
					System.out.println(obj);
					if (obj instanceof MigratableProcess) {

						MigratableProcess mp = (MigratableProcess) obj;
						ProcessThread pt = new ProcessThread(mp, this);
						new Thread(pt).start();
						idsToProcesses.put(mp.getProcessID(), pt);
					} else if (obj instanceof ProcessControlMessage) {
						handleControlMessage((ProcessControlMessage) obj);
					}

				}

			} catch (IOException e) {
				System.err.println("Master disconnected.");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handleControlMessage(ProcessControlMessage pcm) {
		ProcessThread procHandle = idsToProcesses.get(pcm.getProcessID());
		switch (pcm.getCommand()) {
		case START:
			procHandle.unSuspend();
			// idsToProcesses.get(pcm.getProcessID()).
			break;
		case SUSPEND:
			try {
				procHandle.suspend();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case MIGRATE:
			try {
				procHandle.suspend();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ObjectOutputStream oos = new ObjectOutputStream(new
			// TransactionalFileOutputStream(generateProcessFilename(procHandle)));
			// clientSocket.getOutputStream(); //Write message back to master.
			break;

		}
	}

	private String generateProcessFilename(ProcessThread procHandle) {
		return "wow doge. So processID: "
				+ procHandle.getProcess().getProcessID();
	}

	/**
	 * 
	 * @param process
	 *            the process
	 */
	public void ackDone(MigratableProcess process) {
		try {
			this.outStream.writeObject(new DoneMessage(process.getProcessID(),
					process.getClientID()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
