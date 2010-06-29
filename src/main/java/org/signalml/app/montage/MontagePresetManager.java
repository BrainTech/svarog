/* MontagePresetManager.java created 2007-10-24
 *
 */

package org.signalml.app.montage;

import org.signalml.app.config.preset.AbstractPresetManager;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.Montage;

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

}
