package processmigration.processes;

import processmigration.processes.GrepProcess;
import static org.junit.Assert.*;
import org.junit.*;
import org.apache.commons.io.FileUtils;
import java.io.File;

public class TestGrepProcess {

	private void runTest(String pattern, String in, String out, String expected)
			throws Exception {
		String[] Args = { pattern, in, out };
		GrepProcess g = new GrepProcess(Args);
		g.run();

		File of = new File(out);
		String o = FileUtils.readFileToString(of, "US-ASCII");
		File ef = new File(expected);
		String e = FileUtils.readFileToString(ef, "US-ASCII");

		assertEquals(o, e);
	}

	@Test
	public void test1() throws Exception {
		runTest("foo", "build/resources/test/TestGrepProcess/in1.txt",
				"build/resources/test/TestGrepProcess/out1.txt",
				"build/resources/test/TestGrepProcess/expected1.txt");
	}

	@Test
	public void test2() throws Exception {
		runTest("st", "build/resources/test/TestGrepProcess/in2.txt",
				"build/resources/test/TestGrepProcess/out2.txt",
				"build/resources/test/TestGrepProcess/expected2.txt");
	}

}
