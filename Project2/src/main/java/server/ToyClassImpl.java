package server;

import toys.Remote440Exception;
import toys.ToyClass;

public class ToyClassImpl implements ToyClass {

	@Override
	public String printMessage(String message) throws Remote440Exception {
		System.out.println("Hi! Im a toy!");
		System.out.println("Your message was: " + message);
		return "MURDER ME: " + message;
	}

}
