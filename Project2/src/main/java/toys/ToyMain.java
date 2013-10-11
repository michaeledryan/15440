package toys;

import java.rmi.RemoteException;

import messages.Message;
import messages.MessageInterpreter;

public class ToyMain {

	public static void main(String[] args) throws RemoteException {
		Class<?>[] clazzes = { String.class };

		Object[] objs = { "FUCK" };

		Message message = Message.newRequest("", 1099, "printMessage",
				"test toy", objs, clazzes);

		MessageInterpreter interpreter = new MessageInterpreter(message);

		Message response = parseResponse(interpreter.call());
		System.out.println(response.getReturnVal());

		System.out.println("TRYING FOR EXCEPTION...");

		message = Message.newRequest("", 1099, "printMessage", "test toy",
				objs, null);

		interpreter = new MessageInterpreter(message);

		response = parseResponse(interpreter.call());
		
		Message testSerialization;

		System.out.println(response.getReturnVal());

	}

	static public Message parseResponse(Object obj) throws RemoteException {
		Message response = null;
		if (obj instanceof Exception) {
			try {
				throw (Exception) obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (obj instanceof Message) {
			response = (Message) obj;
		}
		
		return response;
	}
}
