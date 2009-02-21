/* RawSignalMRUDEntry.java created 2008-01-30
 * 
 */

package org.signalml.app.document;

import org.signalml.domain.signal.raw.RawSignalDescriptor;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** RawSignalMRUDEntry
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mrud-rawsignal")
public class RawSignalMRUDEntry extends MRUDEntry {

	private RawSignalDescriptor descriptor;
	
	protected RawSignalMRUDEntry() {
		super();
	}
	
	public RawSignalMRUDEntry(ManagedDocumentType documentType, Class<?> documentClass, String path, RawSignalDescriptor descriptor) {
		super(documentType, documentClass, path);
		this.descriptor = descriptor;
	}

	public RawSignalDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(RawSignalDescriptor descriptor) {
		this.descriptor = descriptor;
	}

}
