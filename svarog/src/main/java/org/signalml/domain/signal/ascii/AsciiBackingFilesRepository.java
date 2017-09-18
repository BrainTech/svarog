package org.signalml.domain.signal.ascii;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import org.signalml.app.SvarogApplication;
import org.signalml.util.Util;

/**
 * Manages binary temporary files for reading from ASCII data.
 *
 * ASCII files cannot be effectively read with SignalML, since they consist
 * of variable-length lines. Reading ASCII files in Svarog requires prior
 * conversion to RAW+XML data, stored as temporary files.
 * This singleton class provides these temporary binary and XML files.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class AsciiBackingFilesRepository {

	private static final HashMap<String, AsciiBackingFileEntry> entries = new HashMap<>();

	private AsciiBackingFilesRepository() {
		// cannot be instantiated
	}

	/**
	 * Prepare RAW+XML data set for given ASCII signal file.
	 *
	 * @param asciiFile
	 * @return {@link AsciiBackingFileEntry} with two {@link File}s: raw and xml
	 * @throws IOException
	 */
	public static AsciiBackingFileEntry prepare(File asciiFile) throws IOException {
		String path = asciiFile.getCanonicalPath();
		if (entries.containsKey(path)) {
			AsciiBackingFileEntry entry = entries.get(path);
			if (entry.raw.lastModified() > asciiFile.lastModified()) {
				return entries.get(path);
			}
		}

		File profileDir = SvarogApplication.getSharedInstance().getProfileDir();
		File tempRootDir = new File(profileDir.getAbsolutePath(), "temp");
		if (tempRootDir.exists() && !tempRootDir.isDirectory())
			throw new IOException("can not create the directory for temporary files");
		if (!tempRootDir.exists())
			tempRootDir.mkdir();

		File tempDir = Files.createTempDirectory(tempRootDir.toPath(), "temp").toFile();
		tempDir.deleteOnExit();

		File rawFile = new File(tempDir, asciiFile.getName());
		File xmlFile = Util.changeOrAddFileExtension(rawFile, "xml");
		xmlFile.deleteOnExit();
		rawFile.deleteOnExit();

		AsciiToRawSignalConverter converter = new AsciiToRawSignalConverter();
		converter.convertAsciiSignalToXml(asciiFile, rawFile, xmlFile);

		AsciiBackingFileEntry entry = new AsciiBackingFileEntry(rawFile, xmlFile);
		entries.put(path, entry);
		return entry;
	}

}
