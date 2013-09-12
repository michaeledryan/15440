package common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Util {
	
	public String readFile (String filename) throws IOException {
		
		File file = new File(filename);
		return FileUtils.readFileToString(file);

	}

}
