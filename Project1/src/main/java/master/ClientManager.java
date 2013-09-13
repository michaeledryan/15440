package master;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.ClientRequest;

public class ClientManager implements Runnable {

	private int uuid;
	private Socket sock = null;
	private ConcurrentLinkedQueue<ClientRequest> workQueue;

	// private OutputStream sockOutput = null;

	public ClientManager(int uuid, Socket sock,
			ConcurrentLinkedQueue<ClientRequest> workQueue) throws IOException {
		this.uuid = uuid;
		this.sock = sock;
		this.workQueue = workQueue;
		// sockOutput = sock.getOutputStream();
	}

	public void sendResponse(String message) throws IOException {
		OutputStream out = sock.getOutputStream();
		out.write(message.getBytes());
	}

	@Override
	public void run() {

		System.out.println("Starting socket read.");
		ObjectInputStream in;

		while (true) {
			try {
				in = new ObjectInputStream(sock.getInputStream());
				Object obj = in.readObject();
				if (obj != null && obj instanceof ClientRequest) {
					ClientRequest req = (ClientRequest) obj;
					System.out.printf("Received: pid: %d, command: %s\n",
							req.getProcessId(), req.getRequest());
					req.setClientId(this.uuid);
					workQueue.add(req);
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
