package master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {

	private ServerSocket socket;

	public Listener(int port) {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Listen forever.
		while (true) {
			Socket incoming;
			try {
				incoming = socket.accept();
				ClientManager request = new ClientManager(incoming);
				Thread t = new Thread(request);
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
