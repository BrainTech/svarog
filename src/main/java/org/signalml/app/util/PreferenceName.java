/* PreferenceName.java created 2007-09-10
 * 
 */
package org.signalml.app.util;

/** PreferenceName
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum PreferenceName {

	INITIALIZED ("initialized"),
	PROFILE_DEFAULT ("profileDefault"),
	PROFILE_PATH ("profilePath")
	;
	
	String name;

	private PreferenceName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
		
}
