package worker.processmigration.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import worker.processmigration.io.TransactionalFileOutputStream;

public class TestTransactionalFileOutputStream {

	private static void compare(String x, String y) throws IOException {
		File fx = new File(x);
		File fy = new File(y);

		String tx = FileUtils.readFileToString(fx, "US-ASCII");
		String ty = FileUtils.readFileToString(fy, "US-ASCII");

		assertEquals(tx, ty);
	}

	@Test
	public void test1() throws IOException {
		String s = "build/resources/test/TestTransactionalFileOutputStream/out1.txt";
		TransactionalFileOutputStream t = new TransactionalFileOutputStream(s);

		for (int i = 65; i < 65 + 26; i++) {
			t.write(i);
		}
		t.write(10);

		String e = "build/resources/test/TestTransactionalFileOutputStream/expected.txt";
		compare(s, e);
	}

}
