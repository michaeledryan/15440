package toys;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ToyMain {

	
	public static void main(String[] args) {
		Method meth = null;
		
		
		Class[] clazzes = {String.class};
		
		try {
			meth = ToyClass.class.getMethod("printMessage", clazzes);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object[] objs = {"FUCK"};
		
		Method[] methods = ToyClass.class.getMethods();
		
		for (Method m : methods)
			System.out.println(m.getName());
		
		
		try {
			System.out.println(meth.invoke(new ToyClass(), objs ));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
