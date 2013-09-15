package master;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import worker.processmanagement.ProcessControlMessage;
import worker.processmanagement.ProcessControlMessage.ProcessControlCommand;
import common.ClientRequest;
import common.ClientRequestType;

/**
 * IO between master and client for the duration of the connection.
 * 
 * @author acappiel
 * 
 */
public class ClientManager implements Runnable {

	private int uuid;
	private Socket sock = null;
	private OutputStream outStream;
	private ObjectInputStream inStream;
	private ConcurrentLinkedQueue<ClientRequest> workQueue;
	private List<Integer> pidList;

	public List<Integer> getPidList() {
		return pidList;
	}

	/**
	 * Start the ClientManager.
	 * 
	 * @param uuid
	 *            Assigned by the Listener.
	 * @param sock
	 *            Accepted by the Listener.
	 * @param workQueue
	 *            Passed through from LoadBalancer.
	 * @throws IOException
	 */
	public ClientManager(int uuid, Socket sock) throws IOException {
		this.uuid = uuid;
		this.sock = sock;

		// Only create these once!
		this.outStream = sock.getOutputStream();
		this.inStream = new ObjectInputStream(this.sock.getInputStream());

		// Shared among all ClientManager instances.
		this.workQueue = LoadBalancer.getInstance().getWorkQueue();

		// List of the pids of processes started by this client.
		this.pidList = new ArrayList<Integer>();

	}

	/**
	 * Sends a message back to the client.
	 * 
	 * @param message
	 *            Format should be pid:text.
	 * @throws IOException
	 */
	public void sendResponse(String message) throws IOException {
		this.outStream.write(message.getBytes());
	}

	/**
	 * For the life of the socket, wait for new client commands and trigger an
	 * appropriate action. New processes are sent to the centralized queue
	 * Management commands are handled directly.
	 */
	@Override
	public void run() {

		System.out.println("Starting socket read.");

		while (true) {
			try {
				Object obj = this.inStream.readObject();
				if (obj != null && obj instanceof ClientRequest) {
					ClientRequest req = (ClientRequest) obj;
					System.out.printf("Received: pid: %d, command: %s\n",
							req.getProcessId(), req.getRequest());

					req.setClientId(this.uuid);
					System.out.printf("Received: %s\n", req.getRequest());

					switch (req.getType()) {
					case LIST:
						sendResponse("Processes running from this client: "
								+ pidList.toString());
						break;
					case MIGRATE:
						if ((LoadBalancer.getInstance().getPidsToWorkers()
								.get(req.getProcessId())) == null) {
							sendResponse("No process exists with pid: "
									+ req.getProcessId() + ".");
						} else {
							LoadBalancer
									.getInstance()
									.getPidsToWorkers()
									.get(req.getProcessId())
									.sendControlMessage(
											new ProcessControlMessage(
													req.getProcessId(),
													ProcessControlCommand.MIGRATE,
													null));
						}
						break;
					case START:
						workQueue.add(req);
						pidList.add(req.getProcessId());
						break;
					case KILLALL:
						workQueue.add(req);
						break;
					}
				}
			} catch (EOFException e) {
				System.out.println("Client " + uuid + " disconnected.");
				LoadBalancer.getInstance().getClients().remove(this.uuid);
				break;
			} catch (IOException e) {
				System.err
						.println("Socket read failed. Connection with client "
								+ uuid + " lost.");
				LoadBalancer.getInstance().getClients().remove(this.uuid);
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Ending socket read.");
	}

}
