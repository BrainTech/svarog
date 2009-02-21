/* AbstractPresetManager.java created 2007-10-24
 * 
 */

package org.signalml.app.config.preset;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.signalml.app.config.AbstractXMLConfiguration;
import org.signalml.app.util.XMLUtils;

/** AbstractPresetManager
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractPresetManager extends AbstractXMLConfiguration implements PresetManager, Serializable {

	protected static final Logger logger = Logger.getLogger(AbstractPresetManager.class);
	
	protected ArrayList<Preset> presets = new ArrayList<Preset>();
	protected Preset defaultPreset;
	
	protected transient HashMap<String, Integer> presetsByName;
	
    protected transient EventListenerList listenerList = new EventListenerList();		
	
	@Override
	public Preset getPresetAt(int index) {
		return presets.get(index);
	}

	@Override
	public Preset getPresetByName(String name) {
		if( presetsByName == null ) {
			remapNames();
		}
		Integer index = presetsByName.get(name);
		if( index == null ) {
			return null;
		}
		return presets.get(index);
	}

	@Override
	public int getPresetCount() {
		return presets.size();
	}

	@Override
	public Preset[] getPresets() {
		Preset[] arr = new Preset[presets.size()];
		presets.toArray(arr);
		return arr;
	}

	@Override
	public void removePresetAt(int index) {
		Preset oldPreset = presets.remove(index);
		presetsByName = null;
		firePresetRemoved(this, oldPreset);
	}

	@Override
	public boolean removePresetByName(String name) {
		if( presetsByName == null ) {
			remapNames();
		}
		Integer index = presetsByName.get(name);
		if( index == null ) {
			return false;
		}
		removePresetAt(index);
		return true;
	}

	@Override
	public int setPreset(Preset preset) {
		if( presetsByName == null ) {
			remapNames();
		}
		Integer index = presetsByName.get(preset.getName());
		if( index == null ) {
			index = presets.size();
			presets.add(preset);
			presetsByName.put(preset.getName(), index);
			firePresetAdded(this, preset);
		} else {
			Preset oldPreset = presets.get(index.intValue());
			if( oldPreset != preset ) {
				presets.set(index.intValue(), preset);
				firePresetReplaced(this, oldPreset, preset);
			}
		}
		return index;
	}
		
	@Override
	public Preset getDefaultPreset() {
		return defaultPreset;
	}

	@Override
	public void setDefaultPreset(Preset defaultPreset) {
		if( this.defaultPreset != defaultPreset ) {
			Preset oldDefault = this.defaultPreset;
			this.defaultPreset = defaultPreset;
			fireDefaultPresetChanged(this, oldDefault, defaultPreset);
		}
	}
	
	@Override
	public Preset readFromFile(File file) throws IOException {
		logger.debug( "Reading preset from file [" + file.getAbsolutePath() +"]" );
		Object obj = XMLUtils.newObjectFromFile(file, streamer);
		if( !getPresetClass().isInstance(obj) ) {
			throw new IOException("error.badPresetClass");
		}
		return (Preset) obj;
	}

	@Override
	public void writeToFile(File file, Preset preset) throws IOException {
		logger.debug( "Writing ["+preset.getClass().getSimpleName() + "] to file [" + file.getAbsolutePath() +"]" );
		XMLUtils.objectToFile(preset, file, streamer);
	}

	private void remapNames() {
		presetsByName = new HashMap<String, Integer>();
		int cnt = presets.size();
		for( int i=0; i<cnt; i++ ) {
			presetsByName.put(presets.get(i).getName(), i);
		}
		
	}
			
    public void addPresetManagerListener(PresetManagerListener l) {
        listenerList.add(PresetManagerListener.class, l);
    }

    public void removePresetManagerListener(PresetManagerListener l) {
        listenerList.remove(PresetManagerListener.class, l);
    }
	    
    protected void firePresetAdded(Object source, Preset newPreset) {
        Object[] listeners = listenerList.getListenerList();
        PresetManagerEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PresetManagerListener.class) {
                if (e == null) {
                    e = new PresetManagerEvent(source, null, newPreset);
                }
                ((PresetManagerListener)listeners[i+1]).presetAdded(e);
            }          
        }
    }
	
    protected void firePresetRemoved(Object source, Preset oldPreset) {
        Object[] listeners = listenerList.getListenerList();
        PresetManagerEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PresetManagerListener.class) {
                if (e == null) {
                    e = new PresetManagerEvent(source, oldPreset, null);
                }
                ((PresetManagerListener)listeners[i+1]).presetRemoved(e);
            }          
        }
    }
    
    protected void firePresetReplaced(Object source, Preset oldPreset, Preset newPreset) {
        Object[] listeners = listenerList.getListenerList();
        PresetManagerEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PresetManagerListener.class) {
                if (e == null) {
                    e = new PresetManagerEvent(source, oldPreset, newPreset);
                }
                ((PresetManagerListener)listeners[i+1]).presetReplaced(e);
            }          
        }
    }

    protected void fireDefaultPresetChanged(Object source, Preset oldPreset, Preset newPreset) {
        Object[] listeners = listenerList.getListenerList();
        PresetManagerEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PresetManagerListener.class) {
                if (e == null) {
                    e = new PresetManagerEvent(source, oldPreset, newPreset);
                }
                ((PresetManagerListener)listeners[i+1]).defaultPresetChanged(e);
            }          
        }
    }
    

}
