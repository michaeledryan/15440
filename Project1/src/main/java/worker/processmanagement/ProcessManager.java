package processmanagement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import processmigration.MigratableProcess;

public class ProcessManager implements Runnable{

	private MPNode node;
	private Set<MigratableProcess> local = new HashSet<MigratableProcess>();
	private Set<MigratableProcess> migrated = new HashSet<MigratableProcess>();
	private ServerSocket socket;

	public ProcessManager(MPNode node) {
		setNode(node);
		try {
			socket = new ServerSocket(node.getPort());
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
				ObjectInputStream in =new ObjectInputStream(
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
	
	public void registerProcess(MigratableProcess process) {
		local.add(process);
		new Thread(process).run();
	}

	public void migrateProcess(MigratableProcess process, MPNode node) {
		migrated.add(process);
		sendToNode(process, node);
	}

	public MPNode getNode() {
		return node;
	}

	public void setNode(MPNode node) {
		this.node = node;
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
