package toys;

import remote.Remote440;

public interface ToyClass extends Remote440 {
	
	public String printMessage(String message) throws Remote440Exception;
	

}
