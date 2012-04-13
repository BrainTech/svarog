/* MontagePresetManager.java created 2007-10-24
 *
 */

package org.signalml.app.model.montage;

import java.io.File;
import java.io.IOException;
import org.signalml.app.config.preset.AbstractPresetManager;
import org.signalml.app.config.preset.EegSystemsPresetManager;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.EegSystem;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MontagePresetManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("montages")
public class MontagePresetManager extends AbstractPresetManager {

	private static final long serialVersionUID = 1L;
	private EegSystemsPresetManager eegSystemsPresetManager;

	public MontagePresetManager(EegSystemsPresetManager eegSystemsPresetManager) {
		this.eegSystemsPresetManager = eegSystemsPresetManager;
	}

	@Override
	public String getStandardFilename() {
		return "montages.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return Montage.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createMontagePresetStreamer();
		}
		return streamer;
	}

	private XStream createMontagePresetStreamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForMontage(streamer);
		streamer.setMode(XStream.ID_REFERENCES);

		return streamer;
	}

	@Override
	public void readFromPersistence(File file) throws IOException {
		super.readFromPersistence(file);
		matchEegSystemsWithMontages();
	}

	protected void matchEegSystemsWithMontages() {
		for (Preset preset : presets) {
			Montage montagePreset = (Montage) preset;
			EegSystem eegSystem = eegSystemsPresetManager.getEegSystem(montagePreset.getEegSystemName());
			montagePreset.setEegSystem(eegSystem);
		}
	}

}
