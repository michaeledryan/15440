package worker.processmanagement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import worker.processmigration.MigratableProcess;
import worker.processmigration.io.TransactionalFileOutputStream;

public class ProcessRunner implements Runnable{

	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private int port;
	
	private Map<Integer, ProcessThread> idsToProcesses = new ConcurrentHashMap<Integer, ProcessThread>(); 
	
	public ProcessRunner(int port) {
		this.port = port;
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
				
				// TODO: Establish protocol for sending data back to Master
				ObjectInputStream in = new ObjectInputStream(
						clientSocket.getInputStream());
				
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				
				Object obj;
				while ((obj = in.readObject()) != null) {
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
				System.err.println("Error connecting to client on HTTP.");
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
			//idsToProcesses.get(pcm.getProcessID()).
			break;
		case SUSPEND:
			procHandle.suspend();
			break;
		case MIGRATE:
			procHandle.suspend();
//			ObjectOutputStream oos = new ObjectOutputStream(new TransactionalFileOutputStream(generateProcessFilename(procHandle)));
//			clientSocket.getOutputStream(); //Write message back to master.
			break;
			
			}
	}

	private String generateProcessFilename(ProcessThread procHandle) {
		return "wow doge. So processID: " + procHandle.getProcess().getProcessID();
	}
	
	/**
	 * 
	 * @param process the process
	 */
	public void ackDone(MigratableProcess process) {
		try {
			(new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new DoneMessage(process.getProcessID()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public class DoneMessage {
		public int processID;
		
		public DoneMessage(int id) {
			processID = id;
		}
	}
	
}
