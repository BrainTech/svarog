/**
 *
 */
package org.signalml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/** FileUtils
 * class implements copying one file to another one.
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 *
 */
public class FileUtils {

	/**
	 * Copies content of one file to another file
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
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}
}
