package org.signalml.psychopy;

import java.io.File;

public class FilePathValidator {
	public static boolean pathIsValid(String path) {
		return path != null && !path.isEmpty() && pathExists(path);
	}

	public static boolean pathExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static boolean isEmptyDirectory(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			String[] files = file.list();
			return files == null || files.length == 0;
		} else {
			return false;
		}
	}
}


