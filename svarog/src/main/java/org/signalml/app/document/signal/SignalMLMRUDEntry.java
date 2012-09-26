/* SignalMLMRUDEntry.java created 2007-09-20
 *
 */

package org.signalml.app.document.signal;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.mrud.MRUDEntry;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.codec.SignalMLCodec;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Serializable description of a file with a signal stored in it using signalML
 * {@link SignalMLCodec codec}. Contains:
 * <ul>
 * <li>name and id of the codec</li>
 * <li>sampling frequency of the signal</li>
 * <li>number of channels in the signal</li>
 * <li>size of a page of the signal (in seconds) and the number of blocks in a
 * single page</li>
 * <li>calibration of the signal</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
@XStreamAlias("mrud-signalml")
public class SignalMLMRUDEntry extends MRUDEntry {

	private SignalMLDescriptor descriptor;

	public SignalMLMRUDEntry(ManagedDocumentType documentType, Class<?> documentClass, String path, SignalMLDescriptor descriptor) {
		super(documentType, documentClass, path);
		this.descriptor = descriptor;
	}

	public SignalMLMRUDEntry() {
	}

	public SignalMLDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(SignalMLDescriptor descriptor) {
		this.descriptor = descriptor;
	}

}
