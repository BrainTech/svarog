/* ArtifactOutputFormat.java created 2007-11-02
 * 
 */

package org.signalml.method.artifact;

/** ArtifactOutputFormat
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum ArtifactOutputFormat {

	ARF( "*.arf" ),
	TAG( "*.tag" )
	
	;
	
	private String name;

	private ArtifactOutputFormat(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
		
}
