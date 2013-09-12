package worker.processmanagement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import worker.processmigration.MigratableProcess;

public class ProcessRunner implements Runnable{

	private Map<ProcessRunner, MigratableProcess> processes = new HashMap<ProcessRunner, MigratableProcess>();
	private ServerSocket socket;
	private static ProcessRunner instance = null;
	private static int port;
	
	
	public static void setPort(int portNum) {
		port = portNum;
	}

	public static ProcessRunner getInstance() {
		if (instance == null) {
			instance = new ProcessRunner();
		}
		
		return instance;
	}
	
	private ProcessRunner() {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// Listen for clients
		while (true) {
			Socket client;
			try {
				client = socket.accept();
				
				// TODO: Establish protocol for sending data back to Progess
				ObjectInputStream in = new ObjectInputStream(
						client.getInputStream());
				
				Object obj;
				while ((obj = in.readObject()) != null) {
					System.out.println(obj);
					if (obj instanceof MigratableProcess) {
						((MigratableProcess) obj).run();
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

	public void testMessage(MPNode node) {
		String testObject = "TESTING SERIALIZATION";
		Socket clientSocket = null;
		try {
			clientSocket = new Socket(InetAddress.getByName(node.getIP()),
					node.getPort());
			ObjectOutputStream oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(testObject);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ProcessRunner selectRunner() {
		return null;
	}
	
	
	public void registerProcess(MigratableProcess process) {
		ProcessRunner runner = selectRunner();
		runner.registerProcess(process);
	}

	
	private void sendToNode(MigratableProcess process, MPNode node) {

		Socket clientSocket = null;
		try {
			clientSocket = new Socket(InetAddress.getByName(node.getIP()),
					node.getPort());
			ObjectOutputStream oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(process);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
