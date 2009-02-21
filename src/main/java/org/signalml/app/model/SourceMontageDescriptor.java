/* SourceMontageDescriptor.java created 2007-11-24
 * 
 */

package org.signalml.app.model;

import org.signalml.domain.montage.SourceMontage;

/** SourceMontageDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageDescriptor {

	private SourceMontage montage;

	public SourceMontageDescriptor(SourceMontage montage) {
		this.montage = montage;
	}

	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		this.montage = montage;
	}	
	
}
