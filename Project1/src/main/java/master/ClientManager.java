package master;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	public ClientManager(int uuid, Socket sock,
			ConcurrentLinkedQueue<ClientRequest> workQueue) throws IOException {
		this.uuid = uuid;
		this.sock = sock;

		// Only create these once!
		this.outStream = sock.getOutputStream();
		this.inStream = new ObjectInputStream(this.sock.getInputStream());

		// Shared among all ClientManager instances.
		this.workQueue = workQueue;
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
					// Branch here to handle migrate request.
					System.out.printf("Received: %s\n", req.getRequest());
					System.out.println(req.getType());
					if (req.getType() == ClientRequestType.START) {
						System.out.println("got start request");
						workQueue.add(req);
					} else {
						System.out.println("got non-start request");
					}
				}
			} catch (EOFException e) {
				System.out.println("Client disconnected.");
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

		System.out.println("Ending socket read.");
	}

}
