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
		//Object callee = Registry.getName();
		Object callee = new ToyClass();
		
		Method calling = null;
		try {
			calling = callee.getClass().getMethod(meth, null);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			calling.invoke(callee, message.getArgs());
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
}
