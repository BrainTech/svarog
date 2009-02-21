/* PresetEquippedMethodConfigurer.java created 2007-10-28
 * 
 */

package org.signalml.app.method;

import org.signalml.app.config.preset.PresetManager;

/** PresetEquippedMethodConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface PresetEquippedMethodConfigurer extends MethodConfigurer {

	void setPresetManager( PresetManager presetManager );
	
}
