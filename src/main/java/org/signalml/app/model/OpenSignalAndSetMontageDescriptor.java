/* OpenSignalDescriptor.java created 2011-03-06
 *
 */

package org.signalml.app.model;

import org.signalml.domain.montage.Montage;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageDescriptor {

	private OpenDocumentDescriptor openDocumentDescriptor = new OpenDocumentDescriptor();
	private Montage montage;

	public OpenDocumentDescriptor getOpenDocumentDescriptor() {
		return openDocumentDescriptor;
	}

	public void setOpenDocumentDescriptor(OpenDocumentDescriptor openDocumentDescriptor) {
		this.openDocumentDescriptor = openDocumentDescriptor;
	}

	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	public Montage getMontage() {
		return montage;
	}

}
