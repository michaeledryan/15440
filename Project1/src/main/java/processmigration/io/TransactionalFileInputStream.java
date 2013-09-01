package processmigration.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/*
 * First Attempt at TransactionalFileInputStream:
 * 
 * TODO:
 *   - Serialization.
 * 
 * Ways to extend things:
 *   - Keep the file open until migrating. Need some way to indicate this.
 *   - Buffered IO. Need to flush before migrating.
 */

public class TransactionalFileInputStream extends InputStream implements
		Serializable {

	private static final long serialVersionUID = -5132783333806695091L;
	private File file;
	private long position;

	public TransactionalFileInputStream(String name)
			throws FileNotFoundException {
		File f = new File(name);
		if (f.isDirectory()) {
			throw new FileNotFoundException("File is a directory: " + name);
		} else if (!f.isFile()) {
			throw new FileNotFoundException("File does not exist: " + name);
		} else if (!f.canRead()) {
			throw new FileNotFoundException("Cannot read from file: " + name);
		}
		file = f;
		position = 0;
	}

	@Override
	public int read() throws IOException {
		int data;
		FileInputStream f = new FileInputStream(file);
		try {
			f.skip(position);
			data = f.read();
			if (data != -1) { // EOF.
				position++;
			}
		} catch (IOException e) {
			throw new IOException("Failed to read from file " + file.getPath()
					+ " : " + e.getMessage());
		} finally {
			if (f != null) {
				f.close();
			}
		}
		return data;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

	}

	private void readObjectNoData() throws ObjectStreamException {

	}

}
