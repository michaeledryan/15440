package toys;

import util.Message;
import util.MessageInterpreter;

public class ToyMain {

	public static void main(String[] args) {
		Class<?>[] clazzes = { String.class };

		Object[] objs = { "FUCK" };

		Message message = Message.newRequest("", 1099, "printMessage",
				"test toy", objs, clazzes);

		MessageInterpreter interpreter = new MessageInterpreter(message);
		
		System.out.println(interpreter.call());

	}
}
