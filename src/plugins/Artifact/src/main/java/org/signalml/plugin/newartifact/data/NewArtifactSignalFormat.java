/* ArtifactSignalFormat.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.data;

import org.springframework.context.MessageSourceResolvable;

/** ArtifactSignalFormat
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum NewArtifactSignalFormat implements MessageSourceResolvable {

	FLOAT

	;

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "newArtifactMethod.signalFormat." + toString() };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}


}