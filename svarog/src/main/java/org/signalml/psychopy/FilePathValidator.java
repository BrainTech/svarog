package org.signalml.psychopy;

import java.io.File;

public class FilePathValidator {
	public static boolean pathIsValid(String path) {
		return path != null && !path.isEmpty() && pathExists(path);
	}

	public static boolean fileWithPrefixExists(String path) {
		File prefix = new File(path);
		File parentDirectory = prefix.getParentFile();
		if (parentDirectory != null) {
			File[] files = parentDirectory.listFiles();
			if (files != null) {
				for (File file : parentDirectory.listFiles()) {
					if (file.getName().startsWith(prefix.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean pathExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static boolean isDirectory(String path) {
		File file = new File(path);
		return file.isDirectory();
	}

}


