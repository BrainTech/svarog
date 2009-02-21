/* PresetManagerListener.java created 2007-11-24
 * 
 */

package org.signalml.app.config.preset;

import java.util.EventListener;

/** PresetManagerListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface PresetManagerListener extends EventListener {

	void presetAdded( PresetManagerEvent ev );

	void presetRemoved( PresetManagerEvent ev );
	
	void presetReplaced( PresetManagerEvent ev );
	
	void defaultPresetChanged( PresetManagerEvent ev );
	
}
