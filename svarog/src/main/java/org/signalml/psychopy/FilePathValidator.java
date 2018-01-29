package org.signalml.psychopy;

import java.io.File;

public class FilePathValidator {
	public static boolean pathIsValid(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		} else if (!pathExists(path)) {
			return false;
		} else {
			return true;
		}
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


