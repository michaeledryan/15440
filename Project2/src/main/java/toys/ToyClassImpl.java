package toys;

import java.rmi.RemoteException;


public class ToyClassImpl implements ToyClass {

	public ToyClassImpl() {
		super(); 
	}
	
	@Override
	public String printMessage(String message) throws Remote440Exception {
		System.out.println("Hi! Im a toy!");
		System.out.println("Your message was: " + message);
		return "MURDER ME: " + message;
	}
	

}
