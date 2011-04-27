/* RegisterCodecDescriptor.java created 2007-09-18
 *
 */

package org.signalml.app.model;

import java.io.File;

import org.signalml.codec.XMLSignalMLCodec;

/** RegisterCodecDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecDescriptor {

	private XMLSignalMLCodec codec;
	private File sourceFile;
	private String formatName;

	public XMLSignalMLCodec getCodec() {
		return codec;
	}

	public void setCodec(XMLSignalMLCodec codec) {
		this.codec = codec;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

}
