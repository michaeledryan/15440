package master;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import worker.processmanagement.ProcessControlMessage;
import worker.processmanagement.ProcessControlMessage.ProcessControlCommand;
import worker.processmanagement.WorkerResponse;
import worker.processmigration.MigratableProcess;
import common.ClientRequest;

/**
 * Master server's representation of a worker server. Handles all messages to
 * and from a given worker.
 * 
 * @author michaelryan
 * 
 */
public class WorkerInfo implements Runnable {

	private String hostname;
	private int port;
	private Socket sock;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private Thread t;
	private ConcurrentHashMap<Integer, ClientManager> clients;

	/**
	 * 
	 * @param hostname
	 *            hostname of remote worker
	 * @param port
	 *            port for remote worker
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public WorkerInfo(String hostname, int port) throws UnknownHostException,
			IOException {
		this.setHostname(hostname);
		this.setPort(port);
		this.clients = LoadBalancer.getInstance().getClients();
		this.sock = new Socket(hostname, port);
		this.outStream = new ObjectOutputStream(this.sock.getOutputStream());
		this.inStream = new ObjectInputStream(this.sock.getInputStream());
		System.out.printf("Connected to worker: %s on port: %d\n", hostname,
				port);
		this.t = new Thread(this);
		t.start();
	}

	/**
	 * Generates a process to send to a worker.
	 * 
	 * @param req
	 *            Request to be parsed into a MigratableProcess.
	 */
	public void sendToWorker(ClientRequest req) {
		try {

			String[] requestArray = req.getRequest().split(" ", 2);

			Class<?> clazz;
			clazz = Class.forName(requestArray[0]);

			Constructor<?> ctor = clazz.getConstructor(String[].class);
			Object object = ctor.newInstance((Object) requestArray[1]
					.split(" "));

			if (object instanceof MigratableProcess) {

				LoadBalancer.getInstance().getPidsToWorkers()
						.put(req.getProcessId(), this);

				MigratableProcess dp = (MigratableProcess) object;
				dp.setProcessID(req.getProcessId());
				dp.setClientID(req.getClientId());
				this.outStream.writeObject(dp);
			}
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			try {
				clients.get(req.getClientId()).sendResponse(
						"Problem handling the given request: Received error: "
								+ e.getLocalizedMessage());
			} catch (IOException e1) {
				e1.printStackTrace(); // Can't reach client.
			}

		} catch (IOException e) {
			e.printStackTrace();
			// Socket is closed. Cannot reach worker. TODO:
		}
	}

	/**
	 * Main loop for a WorkerInfo. Listens for messages back from remote workers
	 * and either sends information back to the client or migrates the process
	 * to another worker.
	 */
	@Override
	public void run() {

		while (true) {
			try {
				Object obj = this.inStream.readObject();
				if (obj != null && obj instanceof WorkerResponse) {
					WorkerResponse m = (WorkerResponse) obj;
					switch (m.getType()) {
					case PROCESS_FINISHED:
						ClientManager c = this.clients.get(m.getClientID());
						System.out.printf("Completed: pid: %d\n",
								m.getProcessID());
						c.sendResponse(Integer.toString(m.getProcessID()));
						break;
					case PROCESS_SERIALIZED: // TODO: Is this for suspension or
												// migration?
						System.out.println("SERIALIZED PROCESS:"
								+ m.getProcessID());
						WorkerInfo migrantWorker = LoadBalancer.getInstance()
								.getNextWorker();
						migrantWorker.outStream
								.writeObject(new ProcessControlMessage(m
										.getProcessID(),
										ProcessControlCommand.RESTART, m
												.getSerializedFile()
												.getAbsolutePath()));
						LoadBalancer.getInstance().getPidsToWorkers()
								.put(m.getProcessID(), migrantWorker);
						break;
					}
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

	public void sendControlMessage(ProcessControlMessage msg)
			throws IOException {
		outStream.writeObject(msg);
	}
}
