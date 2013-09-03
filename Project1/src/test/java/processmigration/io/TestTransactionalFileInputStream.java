package processmigration.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class TestTransactionalFileInputStream {

	@Test
	public void test1() throws IOException {
	
		String s = "src/test/resources/TestTransactionalFileInputStream/in1.txt";
		TransactionalFileInputStream t = new TransactionalFileInputStream(s);
		int c, i = 65;
		while ((c = t.read()) >= 65) {
			assertEquals(c, i++);
		}
	}

}
