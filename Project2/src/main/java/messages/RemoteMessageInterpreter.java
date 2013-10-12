package messages;

import remote.Remote440Exception;
import server.ObjectTracker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Interpreter for RemoteMessages. After being given a message, an Interpreter
 *
 * TODO: WHAT ABOUT REPLIES???
 * @author michaelryan
 *
 */
public class RemoteMessageInterpreter implements Callable<Object> {

	private RemoteMessage message;

	public RemoteMessageInterpreter(RemoteMessage message) {
		this.message = message;
	}

	@Override
	public Object call() {
		String meth = message.getMeth();
		Class<?>[] clazzes = message.getClasses();
		// Object callee = Registry.getName();
		Object callee = ObjectTracker.getInstance().lookup(message.getName());

		Method calling;

		try {
			calling = callee.getClass().getMethod(meth, clazzes);
		} catch (NoSuchMethodException e) {
			return new Remote440Exception(
					"NoSuchMethodException: could not find method " + meth
							+ "with parameters " + Arrays.toString(clazzes), e);
		} catch (SecurityException e) {
			return new Remote440Exception("Security exception finding method "
					+ meth + "with parameters " + Arrays.toString(clazzes), e);
		}

		Object result;

		try {
			result = calling.invoke(callee, message.getArgs());
		} catch (IllegalAccessException e) {
			return new Remote440Exception("IllegalAccessException finding " +
                    "method "
					+ meth + "with parameters " + Arrays.toString(clazzes), e);
		} catch (IllegalArgumentException e) {
			return new Remote440Exception("Illegal Argument passed to method "
					+ meth + "with parameters " + Arrays.toString(clazzes)
					+ "and arguments " + Arrays.toString(message.getArgs()), e);
		} catch (InvocationTargetException e) {
			return new Remote440Exception("Could not invoke method " + meth
					+ "on object " + message.getName(), e);
		}

		return RemoteMessage.newReply(result);
	}
}
