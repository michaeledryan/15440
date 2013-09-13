package master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.ClientRequest;

public class Listener implements Runnable {

	private ServerSocket socket;
	private ConcurrentLinkedQueue<ClientRequest> workQueue;
	private ConcurrentHashMap<Integer, ClientManager> clients;

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

	@Override
	public void run() {
		Random uuidGen = new Random();
		// Listen forever.
		while (true) {
			Socket incoming;
			try {
				incoming = socket.accept();
				int uuid = uuidGen.nextInt();
				ClientManager request = new ClientManager(uuid,
						incoming, this.workQueue);
				clients.put(uuid, request);
				Thread t = new Thread(request);
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
