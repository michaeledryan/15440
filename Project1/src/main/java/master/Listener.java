package master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.ClientRequest;

/**
 * Waits for new clients to connect. Each is assigned a uuid and given a
 * ClientManager to handle its input.
 * 
 * @author acappiel
 * 
 */
public class Listener implements Runnable {

	private ServerSocket socket;
	private ConcurrentLinkedQueue<ClientRequest> workQueue;
	private ConcurrentHashMap<Integer, ClientManager> clients;

	/**
	 * Start the Listener. There is only one instance of this.
	 * 
	 * @param port
	 *            Specified on the command line (or default).
	 * @param workQueue
	 *            Created by LoadBalancer and passed through to ClientManagers.
	 * @param clients
	 *            Created by LoadBalancer.
	 */
	public Listener(int port, ConcurrentLinkedQueue<ClientRequest> workQueue,
			ConcurrentHashMap<Integer, ClientManager> clients) {
		this.workQueue = workQueue;
		this.clients = clients;
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
				ClientManager request = new ClientManager(uuid, incoming,
						this.workQueue);
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
