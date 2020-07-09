/* AbstractXMLConfiguration.java created 2007-09-14
 *
 */
package org.signalml.app.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.ConversionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.signalml.app.util.XMLUtils;

/**
 * AbstractXMLConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public abstract class AbstractXMLConfiguration {

	@XStreamOmitField
	private static final Logger logger = Logger
			.getLogger(AbstractXMLConfiguration.class);

	@XStreamOmitField
	protected XStream streamer;

	@XStreamOmitField
	protected File profileDir;

	public void writeToXML(File f, XStream streamer) throws IOException {
		logger.debug("Writing [" + getClass().getSimpleName() + "] to file ["
				+ f.getAbsolutePath() + "]");
		XMLUtils.objectToFile(this, f, streamer);
	}

	public void readFromXML(File f, XStream streamer) throws IOException {
		logger.debug("Reading [" + getClass().getSimpleName() + "] from file ["
				+ f.getAbsolutePath() + "]");
		XMLUtils.objectFromFile(this, f, streamer);
	}

	private File getUsableFile(File file) {
		File useFile = null;
		if (file == null) {
			if (profileDir != null) {
				useFile = getStandardFile(profileDir);
			}
		} else {
			useFile = file;
		}
		if (useFile == null) {
			throw new NullPointerException("No file");
		}
		return useFile;
	}

	public void writeToPersistence(File file) throws IOException {
		File usableFile = getUsableFile(file);
		if (usableFile.isDirectory()) {
			/*
			 * TODO: If this configuration was read from multiple files it
			 * should be able to write the changes made to it to the same files
			 * it read it from. For now, this functionality is not needed, so it
			 * is implemented.
			 */
			throw new UnsupportedOperationException(
					"Writing one XML configuration to multiple files is not supported yet.");
		} else {
			writeToXML(usableFile, getStreamer());
		}
	}
	
	public void readFromPersistence(File file) throws IOException {
		File usableFile = getUsableFile(file);
		if (usableFile.isDirectory()) {
			File[] files = usableFile.listFiles();
			readFromMultipleFiles(files);
		} else {
			readFromXML(usableFile, getStreamer());
		}
	}

	/**
	 * Reads this configuration from multiple files and merges the data into one
	 * configuration.
	 *
	 * @param files
	 *            the files from which this configuration should be read
	 * @throws IOException
	 */
	private void readFromMultipleFiles(File[] files) throws IOException {
		for (File f : files) {
			if (f.isDirectory())
				continue;
			try {
				AbstractXMLConfiguration config = this.getClass().newInstance();
				XMLUtils.objectFromFile(config, f, getStreamer());
				this.copyFrom(config);
			} catch (InstantiationException|IllegalAccessException ex) {
				logger.error(ex, ex);
			}
		}
	}

	/**
	 * Copies the contents of some configuration to this configuration.
	 *
	 * @param otherConfig
	 *            the configuration from which the data shoul be copied
	 */
	public void copyFrom(AbstractXMLConfiguration otherConfig) {
		throw new UnsupportedOperationException("XML configuration from directories is not supported for this manager - please  implement the copyFrom method for your XML configuration manager if you want to read one XML configuration from multiple files at once");
	}

	public void maybeReadFromPersistence(String fnf, String oth) {
		try {
			this.readFromPersistence(null);
		} catch (IOException ex) {
			if (ex instanceof FileNotFoundException)
				logger.debug(fnf);
			else
				logger.error(oth, ex);
		} catch (ConversionException ex) {
			logger.error(oth, ex);
		}
	}

	/**
	 * Reads this configuration from the input stream.
	 *
	 * @param inputStream
	 *            the input stream from which the configuration should be read.
	 *            It is automatically closed after all the reading is done.
	 * @throws IOException
	 *             thrown when an I/O error occurs while closing the input
	 *             stream.
	 */
	public void readFromInputStream(InputStream inputStream) throws IOException {
		XMLUtils.objectFromInputStream(this, inputStream, getStreamer());
	}

	public final File getStandardFile(File profileDir) {
		return new File(profileDir.getAbsolutePath() + File.separator
				+ getStandardFilename());
	}

	public abstract String getStandardFilename();

	public XStream getStreamer() {
		return streamer;
	}

	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

}
