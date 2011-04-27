/* SignalMLCodecDescriptor.java created 2007-09-18
 *
 */

package org.signalml.app.config;

import java.io.File;

import org.signalml.codec.CodecException;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.XMLSignalMLCodec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/** SignalMLCodecDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("codec")
public class SignalMLCodecDescriptor {

	@XStreamAlias("class")
	private Class<?> clazz;

	private String formatName;
	private String sourceUID;
	private String description;
	private String sourceFilePath;
	private String cacheDirName;
	private String sourceSignature;

	@XStreamConverter(SubstitutingStringConverter.class)
	private String repositoryDirPath;

	public SignalMLCodecDescriptor() {
	}

	public SignalMLCodecDescriptor(SignalMLCodec codec) {
		this.clazz = codec.getClass();
		this.formatName = codec.getFormatName();
		this.sourceUID = codec.getSourceUID();

		if (codec instanceof XMLSignalMLCodec) {
			XMLSignalMLCodec xmlCodec = (XMLSignalMLCodec) codec;
			this.description = xmlCodec.getDescription();
			this.sourceFilePath = xmlCodec.getSourceFile().getAbsolutePath();
			this.cacheDirName = xmlCodec.getCacheDirName();
			this.sourceSignature = xmlCodec.getSourceSignature();
			this.repositoryDirPath = xmlCodec.getRepositoryDir().getAbsolutePath();
		}
	}

	public SignalMLCodec getCodec() throws CodecException {
		if (clazz.equals(XMLSignalMLCodec.class)) {
			XMLSignalMLCodec codec = new XMLSignalMLCodec(formatName, cacheDirName, new File(repositoryDirPath), sourceSignature, new File(sourceFilePath));
			codec.setDescription(description);
			return codec;
		} else {
			throw new ClassCastException();
		}
	}

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getCacheDirName() {
		return cacheDirName;
	}

	public void setCacheDirName(String cacheDirName) {
		this.cacheDirName = cacheDirName;
	}

	public String getSourceSignature() {
		return sourceSignature;
	}

	public void setSourceSignature(String sourceSignature) {
		this.sourceSignature = sourceSignature;
	}

	public String getRepositoryDirPath() {
		return repositoryDirPath;
	}

	public void setRepositoryDirPath(String repositoryDirPath) {
		this.repositoryDirPath = repositoryDirPath;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public String getSourceUID() {
		return sourceUID;
	}

	public void setSourceUID(String sourceUID) {
		this.sourceUID = sourceUID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
