/* TagSignalIdentification.java created 2007-09-28
 *
 */

package org.signalml.domain.tag;

import java.io.Serializable;

import org.signalml.app.document.TagDocument;
import org.signalml.domain.signal.SignalChecksum;

/**
 * This class represents the identification of a signal.
 * Contains the checksum, filename and the id of the format of the signal.
 * Allows to identify the signal with which {@link TagDocument tag document}
 * is associated.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagSignalIdentification implements Serializable {

	private static final long serialVersionUID = 1L;

        /**
         * the id of the format of the signal
         */
	private String formatId;

        /**
         * the name of the file in which signal is stored
         */
	private String fileName;

        /**
         * the checksum of the signal
         */
        private SignalChecksum checksum;

        /**
         * Creates an empty TagSignalIdentification.
         */
	public TagSignalIdentification() {
	}

        /**
         * Returns the id of the format of the signal.
         * @return the id of the format of the signal
         */
	public String getFormatId() {
		return formatId;
	}

        /**
         * Sets the id of the format of the signal.
         * @param formatId the id of the format of the signal
         */
	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}

        /**
         * Returns the name of the file in which signal is stored.
         * @return the name of the file in which signal is stored
         */
	public String getFileName() {
		return fileName;
	}

        /**
         * Sets the name of the file in which signal is stored.
         * @param fileName the name of the file in which signal is stored
         */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

        /**
         * Returns the checksum of the signal.
         * @return the checksum of the signal
         */
	public SignalChecksum getChecksum() {
		return checksum;
	}

        /**
         * Sets the checksum of the signal.
         * @param checksum the checksum of the signal
         */
	public void setChecksum(SignalChecksum checksum) {
		this.checksum = checksum;
	}

}
