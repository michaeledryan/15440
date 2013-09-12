package common;

import java.io.Serializable;

public class ClientRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5177348684388096883L;
	private String request;
	
	public ClientRequest(String request) {
		this.setRequest(request);
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

}
