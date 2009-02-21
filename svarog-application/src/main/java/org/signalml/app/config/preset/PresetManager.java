/* PresetManager.java created 2007-10-24
 * 
 */

package org.signalml.app.config.preset;

import java.io.File;
import java.io.IOException;

/** PresetManager
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface PresetManager {

	Class<?> getPresetClass();
	
	int getPresetCount();
	Preset[] getPresets();
	Preset getPresetAt(int index);
	Preset getPresetByName(String name);
	
	int setPreset(Preset preset);
	
	void removePresetAt(int index);
	boolean removePresetByName(String name);
	
	void writeToFile( File file, Preset preset ) throws IOException;
	Preset readFromFile( File file ) throws IOException;
	
	void writeToPersistence(File file) throws IOException;
	void readFromPersistence(File file) throws IOException;
	
	Preset getDefaultPreset();
	void setDefaultPreset(Preset defaultPreset);
	
	void addPresetManagerListener(PresetManagerListener l);
	void removePresetManagerListener(PresetManagerListener l);
	
}
