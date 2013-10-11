package server;

import messages.RemoteMessage;
import messages.RemoteMessageInterpreter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Receives a RMI request, runs it, and sends back the result.
 */
public class UnMarshal implements Runnable {

	private Socket sock;
	private RemoteMessage m;
	private ObjectTracker objs;

	public UnMarshal(Socket sock, ObjectTracker objs) {
		this.sock = sock;
		this.objs = objs;
	}

	/**
	 * Wait for the message to show up, then read it.
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void receiveMessage() throws IOException, ClassNotFoundException {
		ObjectInputStream inStream = new ObjectInputStream(
				this.sock.getInputStream());
		Object obj = inStream.readObject();
		if (!(obj instanceof RemoteMessage)) {
			throw new IOException("Received object that is not a RemoteMessage.");
		}
		this.m = (RemoteMessage) obj;
	}

	/**
	 * Send back the result.
	 *
	 * @param resp
	 *            RemoteMessage of type REPLY or Exception.
	 */
	private void sendReply(Object resp) {
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(
					this.sock.getOutputStream());
			outStream.writeObject(resp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the local object and run the request.
	 */
	public void run() {
		try {
			this.receiveMessage();
			Object obj = this.objs.lookup(this.m.getName()); // ??
			RemoteMessageInterpreter mi = new RemoteMessageInterpreter(this.m);
			Object res = mi.call();
			this.sendReply(res);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
