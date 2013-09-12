package worker.processmigration.processes;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import worker.processmigration.processes.GrepProcess;

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
		runTest("foo", "src/test/resources/TestGrepProcess/in1.txt",
				"src/test/resources/TestGrepProcess/out1.txt",
				"src/test/resources/TestGrepProcess/expected1.txt");
	}

	@Test
	public void test2() throws Exception {
		runTest("st", "src/test/resources/TestGrepProcess/in2.txt",
				"src/test/resources/TestGrepProcess/out2.txt",
				"src/test/resources/TestGrepProcess/expected2.txt");
	}

}
