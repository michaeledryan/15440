package master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Waits for new clients to connect. Each is assigned a uuid and given a
 * ClientManager to handle its input.
 * 
 * @author acappiel
 * 
 */
public class Listener implements Runnable {

	private ServerSocket socket;
	private ConcurrentHashMap<Integer, ClientManager> clients;

	/**
	 * Start the Listener. There is only one instance of this.
	 * 
	 * @param port
	 *            Specified on the command line (or default).
	 */
	public Listener(int port) {
		this.clients = LoadBalancer.getInstance().getClients();
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Accept new client connections.
	 */
	@Override
	public void run() {
		Random uuidGen = new Random();
		// Listen forever.
		while (true) {
			Socket incoming;
			try {
				incoming = socket.accept();
				int uuid = uuidGen.nextInt();
				ClientManager request = new ClientManager(uuid, incoming);
				// Add to the clients map.
				clients.put(uuid, request);
				Thread t = new Thread(request);
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
