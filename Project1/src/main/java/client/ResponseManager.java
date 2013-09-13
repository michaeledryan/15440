package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class ResponseManager implements Runnable {

	private static int BUFSIZE = 1024;
	private String prompt;

	private Socket sock;

	public ResponseManager(Socket sock) {
		this.setSock(sock);
		this.setPrompt("");
	}

	@Override
	public void run() {

		System.out.println("Starting socket read.");
		InputStream in;

		while (true) {
			try {
				byte[] buf = new byte[BUFSIZE];
				in = this.sock.getInputStream();
				int count = in.read(buf);
				if (count > 0) {
					System.out.printf("Response: %s\n%s", new String(buf),
							this.prompt);
				}
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

		System.out.println("Ending socket read.");
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
