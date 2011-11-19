package org.signalml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.io.BufferedInputStream;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This class implements methods for manipulating (moving/copying) files.
 *
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FileUtils {

	/**
	 * Copies content of one file to another file.
	 * @param in this file is source of copying process
	 * @param out this file is destination of copying process
	 */
	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

	/**
	 * Copies a resource file from resource to some other non-resource
	 * path.
	 *
	 * @param resourcePath the path of the file within the JAR file that
	 * should be copied (e.g. 'org/signalml/app/config/eegSystems/EEG 10_10.xml')
	 * @param destinationPath the path of the destination file (may be a directory
	 * or a full file path)
	 * @throws FileNotFoundException thrown when the destinationPath could not be found
	 * @throws IOException thrown when an error occured while reading/writing
	 * to buffer
	 */
	public static void copyFileFromResource(String resourcePath, String destinationPath) throws FileNotFoundException, IOException {
		Resource resource = new ClassPathResource(resourcePath);
		InputStream is = new BufferedInputStream(resource.getInputStream());

		if (isDirectory(destinationPath)) {
			destinationPath += File.separator + resource.getFilename();
		}

		OutputStream os = new FileOutputStream(destinationPath);

		byte[] buffer = new byte[4096];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}

		os.close();
		is.close();
	}

	/**
	 * Returns if the given path is a directory.
	 * @param filePath the path to be checked for being a directory
	 * @return true if the given path is a directory, false otherwise
	 */
	public static boolean isDirectory(String filePath) {
		return new File(filePath).isDirectory();
	}

	/**
	 * Creates a directory having a given path. (Does work recursively:
	 * creates all directories on the path if some does not exist).
	 * @param directoryPath a path of the directory to be created
	 * @return true if the directory was sucessfully created, false otherwise
	 */
	public static boolean createDirectory(String directoryPath) {
		return new File(directoryPath).mkdirs();
	}
}
