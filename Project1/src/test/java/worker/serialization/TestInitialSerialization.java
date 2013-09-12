package worker.serialization;

import org.junit.BeforeClass;
import org.junit.Test;

import worker.processmanagement.MPNode;
import worker.processmanagement.ProcessManager;
import worker.processmigration.processes.GrepProcess;

public class TestInitialSerialization {

	static MPNode node1 = new MPNode("127.0.0.1", 8000);
	static MPNode node2 = new MPNode("127.0.0.1", 8001);

	static ProcessManager man1 = new ProcessManager(node1);
	static ProcessManager man2 = new ProcessManager(node2);

	@BeforeClass
	public static void beforeTests() {
		new Thread(man1).start();
		new Thread(man2).start();

	}

	@Test
	public void test() {
		man1.testMessage(node2);
	}

	@Test
	public void testActualSerialization() {
		
		GrepProcess process1 = null;
		try {
			process1 = buildGrep("st",
					"src/test/resources/TestGrepProcess/in2.txt",
					"src/test/resources/TestGrepProcess/out2.txt");
			process1.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		man1.migrateProcess(process1, node2);
	}
	
	@Test
	public void testLargeInput() {
		
		GrepProcess p = null;
		try {
			p = buildGrep("cat",
					"src/test/resources/TestGrepProcess/twitter_1k.txt",
					"src/test/resources/TestGrepProcess/twitter_1k-out.txt");
			p.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//man1.migrateProcess(p, node2);
	}

	private GrepProcess buildGrep(String pattern, String in, String out)
			throws Exception {
		String[] Args = { pattern, in, out };
		return new GrepProcess(Args);

	}
}
