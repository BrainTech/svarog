/* SignalExportPresetManager.java created 2008-01-26
 *
 */

package org.signalml.app.config.preset;

import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.util.XMLUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SignalExportPresetManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("signalexports")
public class SignalExportPresetManager extends AbstractPresetManager {

	private static final long serialVersionUID = 1L;

	@Override
	public String getStandardFilename() {
		return "signal-export.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return SignalExportDescriptor.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createSignalExportPresetStreamer();
		}
		return streamer;
	}

	private XStream createSignalExportPresetStreamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForSignalExport(streamer);
		streamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);

		return streamer;
	}

}
