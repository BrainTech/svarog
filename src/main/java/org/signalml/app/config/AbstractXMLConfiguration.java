/* AbstractXMLConfiguration.java created 2007-09-14
 *
 */
package org.signalml.app.config;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.signalml.app.util.XMLUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.InputStream;

/** AbstractXMLConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractXMLConfiguration {

	@XStreamOmitField
	private static final Logger logger = Logger.getLogger(AbstractXMLConfiguration.class);

	@XStreamOmitField
	protected XStream streamer;

	@XStreamOmitField
	protected File file;

	@XStreamOmitField
	protected File profileDir;

	public void writeToXML(File f, XStream streamer) throws IOException {
		logger.debug("Writing ["+getClass().getSimpleName() + "] to file [" + f.getAbsolutePath() +"]");
		XMLUtils.objectToFile(this, f, streamer);
	}

	public void readFromXML(File f, XStream streamer) throws IOException {
		logger.debug("Reading ["+getClass().getSimpleName() + "] from file [" + f.getAbsolutePath() +"]");
		XMLUtils.objectFromFile(this, f, streamer);
	}

	private File getUsableFile(File file) {
		File useFile = null;
		if (file == null) {
			if (this.file == null) {
				if (profileDir != null) {
					useFile = getStandardFile(profileDir);
				}
			} else {
				useFile = this.file;
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
		writeToXML(getUsableFile(file), getStreamer());
	}

	public void readFromPersistence(File file) throws IOException {
		readFromXML(getUsableFile(file), getStreamer());
	}

	public void maybeReadFromPersistence(String fnf, String oth) {
		try {
			this.readFromPersistence(null);
		} catch (IOException ex) {
			if (ex instanceof FileNotFoundException)
				logger.debug(fnf);
			else
				logger.error(oth, ex);
		}
	}

	/**
	 * Reads this configuration from the input stream.
	 * @param inputStream the input stream from which the configuration
	 * should be read. It is automatically closed after all the reading
	 * is done.
	 * @throws IOException thrown when an I/O error occurs while closing
	 * the input stream.
	 */
	public void readFromInputStream(InputStream inputStream) throws IOException {
		XMLUtils.objectFromInputStream(this, inputStream, getStreamer());
	}

	public final File getStandardFile(File profileDir) {
		return new File(profileDir.getAbsolutePath()+File.separator+getStandardFilename());
	}

	public abstract String getStandardFilename();

	public XStream getStreamer() {
		return streamer;
	}

	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

}
