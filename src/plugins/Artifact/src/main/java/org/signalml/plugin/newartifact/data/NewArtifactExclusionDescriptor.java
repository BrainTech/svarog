/* ArtifactExclusionDescriptor.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.data;

import org.signalml.domain.montage.SourceMontage;

/** ArtifactExclusionDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactExclusionDescriptor {

	private SourceMontage montage;
	private int[][] exclusion;

	public NewArtifactExclusionDescriptor(SourceMontage montage, int[][] exclusion) {
		this.montage = montage;
		this.exclusion = exclusion;
	}

	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		this.montage = montage;
	}

	public int[][] getExclusion() {
		return exclusion;
	}

	public void setExclusion(int[][] exclusion) {
		this.exclusion = exclusion;
	}

}