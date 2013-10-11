package toys;
public class ToyClass {

	public ToyClass() {
		super(); 
	}
	
	public String printMessage(String message) {
		System.out.println("Hi! Im a toy!");
		System.out.println("Your message was: " + message);
		return "MURDER ME: " + message;
	}
	

}
