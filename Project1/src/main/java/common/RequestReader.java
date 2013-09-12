package common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RequestReader implements Runnable {

	private Socket sock = null;
	private InputStream sockInput = null;

	// private OutputStream sockOutput = null;

	public RequestReader(Socket sock) throws IOException {
		this.sock = sock;
		sockInput = sock.getInputStream();
		// sockOutput = sock.getOutputStream();
	}

	@Override
	public void run() {

		System.out.println("Starting socket read.");

		try {
			ObjectInputStream in = new ObjectInputStream(sockInput);
			Object obj = in.readObject();
			if (obj != null && obj instanceof ClientRequest) {
				ClientRequest req = (ClientRequest) obj;
				System.out.printf("Received: %s\n", req.getRequest());
			}
		} catch (IOException e) {
			System.err.println("Socket read failed. Aborting.");
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sock.close();
		} catch (Exception e) {
			System.err.println("Failed to close socket.");
			e.printStackTrace();
		}

		System.out.println("Ending socket read.");
	}

}
