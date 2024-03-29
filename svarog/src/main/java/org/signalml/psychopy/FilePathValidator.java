package org.signalml.psychopy;

import java.io.File;

public class FilePathValidator {
	public static boolean pathIsValid(String path) {
		return path != null && !path.isEmpty() && pathExists(path);
	}

	private static boolean pathExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static boolean fileWithPrefixExists(String path) {
		File filePath = new File(path);
		File parentDirectory = filePath.getParentFile();

		String prefix = filePath.getName();

		if (parentDirectory != null) {
			File[] files = parentDirectory.listFiles();
			if (files != null) {
				for (File file : parentDirectory.listFiles()) {
					if (file.isDirectory())
						continue;
					String name = file.getName();					
					if (name.contains(".") && name.substring(0, name.lastIndexOf('.')).equals(prefix)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isDirectory(String path) {
		File file = new File(path);
		return file.isDirectory();
	}

	public static boolean isFile(String path) {
		File file = new File(path);
		return file.isFile();
	}

	public static String cutXmlFileExtension(String path) {
		if (path.endsWith(".xml")) {
			return path.substring(0, path.lastIndexOf(".xml"));
		} else {
			return path;
		}
	}

}


