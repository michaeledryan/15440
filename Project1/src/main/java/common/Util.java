package common;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

	// http://m2tec.be/blog/2010/02/03/java-md5-hex-0093
	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

}
