/* ArtifactType.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.data;

import org.springframework.context.MessageSourceResolvable;

/** ArtifactType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum NewArtifactType implements MessageSourceResolvable {

	BREATHING,
	EYE_MOVEMENT,
	MUSCLE_ACTIVITY,
	EYEBLINKS,
	TECHNICAL,
	POWER_SUPPLY,
	ECG,
	UNKNOWN

	;

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "newArtifactMethod.artifactType." + toString() };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}

}