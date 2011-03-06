/* OpenSignalDescriptor.java created 2011-03-06
 *
 */

package org.signalml.app.model;

import org.signalml.domain.montage.Montage;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalDescriptor {

	private OpenFileSignalDescriptor openFileSignalDescriptor;
	private OpenMonitorDescriptor openMonitorDescriptor;
	private Montage montage;

	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	public Montage getMontage() {
		return montage;
	}

}
