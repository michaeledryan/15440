package toys;
public class ToyClass {

	public ToyClass() {
		super(); 
	}
	
	public String printMessage(String message) {
		System.out.print("Hi! Im a toy!");
		System.out.print("Your message was: " + message);
		return "MURDER ME: " + message;
	}
	

}
