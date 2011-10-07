/* PresetManagerEvent.java created 2007-11-24
 *
 */

package org.signalml.app.config.preset;

import java.util.EventObject;

/** PresetManagerEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PresetManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private Preset newPreset;
	private Preset oldPreset;

	public PresetManagerEvent(Object source, Preset oldPreset, Preset newPreset) {
		super(source);
		this.newPreset = newPreset;
		this.oldPreset = oldPreset;
	}

	public Preset getNewPreset() {
		return newPreset;
	}

	public Preset getOldPreset() {
		return oldPreset;
	}

}
