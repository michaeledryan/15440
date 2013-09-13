package master;

import java.lang.reflect.Constructor;

public class argtype {
	static String[] asdf = {"101"};
	public static void main(String[] args) {
		Class<?> clazz;
		try {
			clazz = Class.forName("worker.processmigration.processes.DummyProcess");
		
		Constructor<?> ctor = clazz.getConstructor(String[].class);
		ctor.setAccessible(true);
		Object object = ctor.newInstance((Object) ( (Object[])asdf));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
