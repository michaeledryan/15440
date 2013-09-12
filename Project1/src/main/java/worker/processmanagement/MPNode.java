package worker.processmanagement;

/**
 * Wrapper for information about remote process managers.
 * 
 * @author Michael
 * 
 */
public class MPNode {

	private String ip;
	private int port;

	public MPNode(String ip, int port) {
		this.setIP(ip);
		this.setPort(port);
	}

	public String getIP() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	

}
