package processmigration.io;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8310483022289606755L;

	public TransactionalFileOutputStream(String s, boolean b) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void write(int arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

	}

	private void readObjectNoData() throws ObjectStreamException {

	}

}
