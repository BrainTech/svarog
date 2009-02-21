/* TagSignalIdentification.java created 2007-09-28
 * 
 */

package org.signalml.domain.tag;

import java.io.Serializable;

import org.signalml.domain.signal.SignalChecksum;

/** TagSignalIdentification
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagSignalIdentification implements Serializable {

	private static final long serialVersionUID = 1L;

	private String formatId;	
	private String fileName; 	
	private SignalChecksum checksum;
	
	public TagSignalIdentification() {
	}

	public String getFormatId() {
		return formatId;
	}

	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public SignalChecksum getChecksum() {
		return checksum;
	}

	public void setChecksum(SignalChecksum checksum) {
		this.checksum = checksum;
	}
		
}
