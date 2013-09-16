package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listen for responses from the master.
 * 
 * @author acappiel
 * 
 */
public class ResponseManager implements Runnable {

	private static int BUFSIZE = 1024;
	private String prompt;

	private AtomicInteger waitingCount;
	private Socket sock;
	private InputStream inStream;

	/**
	 * Sit on existing socket.
	 * 
	 * @param sock
	 *            Socket created in Main.
	 * @param waitingCount
	 *            Tracks outstanding requests. Decrement with each received
	 *            message.
	 * @throws IOException
	 */
	public ResponseManager(Socket sock, AtomicInteger waitingCount)
			throws IOException {
		this.setSock(sock);
		this.inStream = this.sock.getInputStream();
		this.waitingCount = waitingCount;

		// Just to keep things looking nice in interactive mode.
		this.setPrompt("");
	}

	/**
	 * Listen until disconnected.
	 */
	@Override
	public void run() {

		while (true) {
			try {
				// Assumes buffer holds the full message.
				byte[] buf = new byte[BUFSIZE];
				int count = this.inStream.read(buf);
				if (count > 0) {
					System.out.printf("Response: %s\n%s", new String(buf),
							this.prompt);
				}
				this.waitingCount.getAndDecrement();
			} catch (EOFException e) {
				System.out.println("Master disconnected.");
				break;
			} catch (SocketException e) {
				System.err.println("Socket disconnected.");
				break;
			} catch (IOException e) {
				System.err.println("Socket read failed. Aborting.");
				e.printStackTrace();
				break;
			}
		}

	}

	public Socket getSock() {
		return this.sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

}
