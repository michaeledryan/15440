package master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.RequestReader;

public class Listener implements Runnable {
	
	private ServerSocket socket;
	
	public Listener (int port) {
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
				RequestReader request = new RequestReader(incoming);
				request.run();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
