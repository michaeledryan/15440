package processmigration.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.ObjectStreamException;

public class TransactionalFileInputStream extends InputStream implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5132783333806695091L;

	public TransactionalFileInputStream(String s) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

	}

	private void readObjectNoData() throws ObjectStreamException {

	}

}
