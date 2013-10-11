package toys;

/**
 * Exception thrown whenever something RMI-related goes wrong.
 * 
 * @author Michael Ryan and Alex Capiello 
 */
public class Remote440Exception extends Exception {

	public Remote440Exception(String string) {
		super(string);
	}
	
	public Remote440Exception(String string, Throwable t) {
		super(string, t);
	}

	private static final long serialVersionUID = -3347871942185605138L;

}
