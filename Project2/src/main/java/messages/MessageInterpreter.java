package messages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import toys.ToyClass;

public class MessageInterpreter implements Callable<Object> {

	private Message message;

	public MessageInterpreter(Message message) {
		this.message = message;
	}

	@Override
	public Object call() {
		String meth = message.getMeth();
		Class<?>[] clazzes = message.getClasses();
		// Object callee = Registry.getName();
		Object callee = new ToyClass();

		Method calling = null;

		try {
			calling = callee.getClass().getMethod(meth, clazzes);
		} catch (NoSuchMethodException e) {
			return new RemoteException(
					"NoSuchMethodException: could not find method " + meth
							+ "with parameters " + clazzes, e);
		} catch (SecurityException e) {
			return new RemoteException("Security exception finding method "
					+ meth + "with parameters " + clazzes, e);
		}

		Object result = null;

		try {
			result = calling.invoke(callee, message.getArgs());
		} catch (IllegalAccessException e) {
			return new RemoteException("IllegalAccessException finding method "
					+ meth + "with parameters " + clazzes, e);
		} catch (IllegalArgumentException e) {
			return new RemoteException("Illegal Argument passed to method "
					+ meth + "with parameters " + clazzes.toString()
					+ "and arguments " + message.getArgs(), e);
		} catch (InvocationTargetException e) {
			return new RemoteException("Could not invoke method " + meth
					+ "on object " + message.getName(), e);
		}

		return Message.newReply(result);

	}
}
