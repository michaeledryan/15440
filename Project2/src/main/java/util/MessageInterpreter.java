package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
		Class<?>[] clazzes =  message.getClasses();
		//Object callee = Registry.getName();
		Object callee = new ToyClass();
		
		Method calling = null;
		
		try {
			calling = callee.getClass().getMethod(meth, clazzes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			// TODO: Return a RemoteException??
		}
		
	
		Object result = null;
		
		try {
			result = calling.invoke(callee, message.getArgs());
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			//TODO: Also RemoteException?? 
			e.printStackTrace();
		}
		
		return result;
		
	}
}
