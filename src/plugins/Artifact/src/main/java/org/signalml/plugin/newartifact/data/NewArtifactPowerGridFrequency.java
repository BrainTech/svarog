/* ArtifactPowerGridFrequency.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.data;

import org.springframework.context.MessageSourceResolvable;

/** ArtifactPowerGridFrequency
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum NewArtifactPowerGridFrequency implements MessageSourceResolvable {

	EUROPE(50F),
	USA(60F)

	;

	private float frequency;

	private NewArtifactPowerGridFrequency(float frequency) {
		this.frequency = frequency;
	}

	public float getFrequency() {
		return frequency;
	}

	public static NewArtifactPowerGridFrequency forFloat(float powerGridFrequency) {
		NewArtifactPowerGridFrequency[] all = values();
		for (int i=0; i<all.length; i++) {
			if (all[i].frequency == powerGridFrequency) {
				return all[i];
			}
		}
		return null;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "newArtifactMethod.powerGridFrequency." + toString() };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}

}