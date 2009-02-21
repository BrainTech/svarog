/* Preset.java created 2007-10-24
 * 
 */

package org.signalml.app.config.preset;

import java.io.Serializable;

/** Preset
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Preset extends Serializable {

	String getName();
	void setName(String name);
	
}
