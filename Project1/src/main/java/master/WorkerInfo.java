package master;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import worker.processmanagement.DoneMessage;
import worker.processmigration.MigratableProcess;
import worker.processmigration.processes.DummyProcess;

import common.ClientRequest;

public class WorkerInfo implements Runnable {

	private String hostname;
	private int port;
	private Socket sock;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private Thread t;
	private ConcurrentHashMap<Integer, ClientManager> clients;

	public WorkerInfo(String hostname, int port,
			ConcurrentHashMap<Integer, ClientManager> clients)
			throws UnknownHostException, IOException {
		this.setHostname(hostname);
		this.setPort(port);
		this.clients = clients;
		this.sock = new Socket(hostname, port);
		this.outStream = new ObjectOutputStream(this.sock.getOutputStream());
		this.inStream = new ObjectInputStream(this.sock.getInputStream());
		System.out.printf("Connected to worker: %s on port: %d\n", hostname,
				port);
		this.t = new Thread(this);
		t.start();
	}

	public void sendToWorker(ClientRequest req) {
		// Assuming it's a DummyProcess....

		try {
			String[] requestArray = req.getRequest().split(" ", 2);
			MigratableProcess dp = new DummyProcess(requestArray[1].split(" "));

			dp.setProcessID(req.getProcessId());
			dp.setClientID(req.getClientId());
			this.outStream.writeObject(dp);

			// getRunner().sendProcess(dp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void run() {

		while (true) {
			try {
				Object obj = this.inStream.readObject();
				if (obj != null && obj instanceof DoneMessage) {
					DoneMessage m = (DoneMessage) obj;
					ClientManager c = this.clients.get(m.clientID);
					System.out.printf("Completed: pid: %d\n", m.processID);
					c.sendResponse(Integer.toString(m.processID));
				}
			} catch (EOFException e) {
				System.out.println("Disconnected.");
				break;
			} catch (IOException e) {
				System.err.println("Socket read failed. Aborting.");
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
