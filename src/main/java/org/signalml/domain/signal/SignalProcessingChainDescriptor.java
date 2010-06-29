/* SignalProcessingChainDescriptor.java created 2008-01-27
 *
 */

package org.signalml.domain.signal;

import org.signalml.app.document.MRUDEntry;
import org.signalml.domain.montage.Montage;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SignalProcessingChainDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sigprocchain")
public class SignalProcessingChainDescriptor {

	private SignalType type;

	private MRUDEntry document;
	private Montage montage;

	private boolean sourceBuffered;
	private boolean assembled;
	private boolean montageBuffered;
	private boolean filtered;

	public SignalProcessingChainDescriptor() {
	}

	public SignalType getType() {
		return type;
	}

	public void setType(SignalType type) {
		this.type = type;
	}

	public MRUDEntry getDocument() {
		return document;
	}

	public void setDocument(MRUDEntry document) {
		this.document = document;
	}

	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	public boolean isSourceBuffered() {
		return sourceBuffered;
	}

	public void setSourceBuffered(boolean sourceBuffered) {
		this.sourceBuffered = sourceBuffered;
	}

	public boolean isAssembled() {
		return assembled;
	}

	public void setAssembled(boolean assembled) {
		this.assembled = assembled;
	}

	public boolean isMontageBuffered() {
		return montageBuffered;
	}

	public void setMontageBuffered(boolean montageBuffered) {
		this.montageBuffered = montageBuffered;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

}
