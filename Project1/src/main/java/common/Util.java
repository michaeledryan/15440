package common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Collection of utility functions.
 * 
 * @author acappiel
 * 
 */
public class Util {

	/**
	 * Read file contents as string.
	 * 
	 * @param filename
	 *            A path.
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String filename) throws IOException {

		File file = new File(filename);
		return FileUtils.readFileToString(file);

	}

}
