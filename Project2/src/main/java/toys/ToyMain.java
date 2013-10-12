package toys;

import messages.RemoteMessage;
import messages.RemoteMessageInterpreter;
import remote.Remote440Exception;

public class ToyMain {

	public static void main(String[] args) throws Remote440Exception {
		Class<?>[] clazzes = { String.class };

		Object[] objs = { "FUCK" };

		RemoteMessage message = RemoteMessage.newRequest("", 1099, "printMessage",
                "test toy", objs, clazzes);

		RemoteMessageInterpreter interpreter = new RemoteMessageInterpreter(message);

		RemoteMessage response = parseResponse(interpreter.call());
		System.out.println(response.getReturnVal());

		System.out.println("TRYING FOR EXCEPTION...");

		message = RemoteMessage.newRequest("", 1099, "printMessage", "test toy",
                objs, null);

		interpreter = new RemoteMessageInterpreter(message);

		response = parseResponse(interpreter.call());
		System.out.println(response.getReturnVal());

	}

	static public RemoteMessage parseResponse(Object obj)
            throws Remote440Exception {
		RemoteMessage response = null;
		if (obj instanceof Exception) {
			try {
				throw (Exception) obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (obj instanceof RemoteMessage) {
			response = (RemoteMessage) obj;
		}

		return response;
	}
}
