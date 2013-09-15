package master;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import common.ClientRequest;
import common.ClientRequestType;

/**
 * The LoadBalancer is the primary thread of the master. Centralized data
 * structures are created here and initial communication with the workers
 * established.
 * 
 * @author acappiel
 * 
 */
public class LoadBalancer implements Runnable {

	private Listener L;
	private Thread listener;
	private int port;
	private WorkerInfo[] workers;
	private AtomicInteger nextWorker;
	private ConcurrentLinkedQueue<ClientRequest> workQueue;
	private ConcurrentHashMap<Integer, ClientManager> clients;
	private ConcurrentHashMap<Integer, WorkerInfo> pidsToWorkers;
	
	private static LoadBalancer INSTANCE = null;
	
	
	public static LoadBalancer getInstance() {
		return INSTANCE;
	}
	
	public static LoadBalancer initLoadBalancer(int port, String workers) throws UnknownHostException, IOException {
		INSTANCE = new LoadBalancer(port, workers);
		return getInstance();
	}

	/**
	 * Initialize central data structures and contact workers.
	 * 
	 * @param port
	 *            From command line (or default).
	 * @param workers
	 *            Newline-delimited list of workers.
	 *            Format is hostname:port or ip:port.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private LoadBalancer(int port, String workers) throws UnknownHostException,
			IOException {
		this.port = port;
		this.nextWorker = new AtomicInteger();
		this.workQueue = new ConcurrentLinkedQueue<ClientRequest>();
		this.clients = new ConcurrentHashMap<Integer, ClientManager>();
		this.pidsToWorkers = new ConcurrentHashMap<Integer, WorkerInfo>();

		String[] workerList = workers.split("\n");
		this.workers = new WorkerInfo[workerList.length];
		for (int i = 0; i < workerList.length; i++) {
			String[] worker = workerList[i].split(":");
			if (worker.length < 2) {
				this.workers[i] = new WorkerInfo(worker[0], 8001, this.clients);
			} else {
				int workerPort = Integer.parseInt(worker[1]);
				this.workers[i] = new WorkerInfo(worker[0], workerPort,
						this.clients);
			}
		}
	}

	public ConcurrentLinkedQueue<ClientRequest> getWorkQueue() {
		return workQueue;
	}

	public ConcurrentHashMap<Integer, ClientManager> getClients() {
		return clients;
	}

	public ConcurrentHashMap<Integer, WorkerInfo> getPidsToWorkers() {
		return pidsToWorkers;
	}

	/**
	 * Assign new work and redistribute (migrate) existing work.
	 */
	@Override
	public void run() {
		L = new Listener(this.port, this.workQueue, this.clients);
		this.listener = new Thread(L);
		this.listener.start();

		while (true) {
			while (this.workQueue.isEmpty()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ClientRequest req = this.workQueue.poll();
			if (req.getType() == ClientRequestType.START) {
				this.workers[this.nextWorker.getAndIncrement()].sendToWorker(req);
				this.nextWorker.set( this.nextWorker.get() % this.workers.length);
			} else {
				pidsToWorkers.get(req.getProcessId()).sendToWorker(req);
			}
		}
	}
	
	public WorkerInfo getNextWorker() {
		WorkerInfo worker = this.workers[this.nextWorker.getAndIncrement()];
		this.nextWorker.set( this.nextWorker.get() % this.workers.length);
		return worker;
	}

}
