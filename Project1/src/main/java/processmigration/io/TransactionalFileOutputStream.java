package processmigration.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;

/*
 * First Attempt at TransactionalFileOutputStream:
 * 
 * TODO:
 *   - Serialization.
 * 
 * Ways to extend things:
 *   - Keep the file open until migrating. Need some way to indicate this.
 *   - Buffered IO. Need to flush before migrating.
 */

public class TransactionalFileOutputStream extends OutputStream implements
		Serializable {

	private static final long serialVersionUID = 8310483022289606755L;
	private File file;

	public TransactionalFileOutputStream(String name)
			throws FileNotFoundException, IOException {
		this(name, false);
	}

	public TransactionalFileOutputStream(String name, boolean append)
			throws FileNotFoundException, IOException {
		File f = new File(name);
		if (f.isDirectory()) {
			throw new FileNotFoundException("File is a directory: " + name);
		}
		if (!f.exists()) {
			f.createNewFile();
		}
		if (!f.canWrite()) {
			throw new FileNotFoundException("Cannot write to file: " + name);
		}
		// Is there a more sensible way to accomplish this?
		if (!append) {
			FileOutputStream out = new FileOutputStream(f);
			out.write(0);
			out.close();
		}
		file = f;
	}

	@Override
	public void write(int b) throws IOException {
		// Always append to the file.
		FileOutputStream f = new FileOutputStream(file, true);
		try {
			f.write(b);
		} catch (IOException e) {
			throw new IOException("Failed to write to file " + file.getPath()
					+ " : " + e.getMessage());
		} finally {
			if (f != null) {
				f.close();
			}
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

	}

	private void readObjectNoData() throws ObjectStreamException {

	}

}
