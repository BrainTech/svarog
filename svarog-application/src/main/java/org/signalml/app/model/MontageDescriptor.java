/* MontageDescriptor.java created 2007-11-24
 * 
 */

package org.signalml.app.model;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.Montage;

/** MontageDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageDescriptor {

	private Montage montage;
	private SignalDocument signalDocument; 
		
	public MontageDescriptor(Montage montage, SignalDocument signalDocument) {
		this.montage = montage;
		this.signalDocument = signalDocument;
	}

	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	public SignalDocument getSignalDocument() {
		return signalDocument;
	}

	public void setSignalDocument(SignalDocument signalDocument) {
		this.signalDocument = signalDocument;
	}
	
}
