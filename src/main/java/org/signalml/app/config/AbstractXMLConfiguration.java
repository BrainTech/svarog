/* AbstractXMLConfiguration.java created 2007-09-14
 * 
 */
package org.signalml.app.config;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.util.XMLUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

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
		
	public void writeToXML( File f, XStream streamer ) throws IOException {
		logger.debug( "Writing ["+getClass().getSimpleName() + "] to file [" + f.getAbsolutePath() +"]" );
		XMLUtils.objectToFile(this, f, streamer);
	}
	
	public void readFromXML( File f, XStream streamer ) throws IOException {
		logger.debug( "Reading ["+getClass().getSimpleName() + "] from file [" + f.getAbsolutePath() +"]" );
		XMLUtils.objectFromFile(this, f, streamer);
	}
		
	private File getUsableFile(File file) {
		File useFile = null;
		if( file == null ) {
			if( this.file == null ) {
				if( profileDir != null ) {
					useFile = getStandardFile(profileDir);
				}
			} else {
				useFile = this.file;
			}			
		} else {
			useFile = file;
		}
		if( useFile == null ) {
			throw new NullPointerException( "No file" );
		}
		return useFile;
	}
	
	public void writeToPersistence(File file) throws IOException {
		writeToXML( getUsableFile(file), getStreamer() );
	}

	public void readFromPersistence(File file) throws IOException {
		readFromXML( getUsableFile(file), getStreamer());
	}
	
	public final File getStandardFile( File profileDir ) {
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
