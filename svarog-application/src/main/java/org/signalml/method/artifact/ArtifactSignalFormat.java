/* ArtifactSignalFormat.java created 2007-11-02
 * 
 */

package org.signalml.method.artifact;

import org.springframework.context.MessageSourceResolvable;

/** ArtifactSignalFormat
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum ArtifactSignalFormat implements MessageSourceResolvable {

	FLOAT
	
	;
	
	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "artifactMethod.signalFormat." + toString() };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}
	
	
}
