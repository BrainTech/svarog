/* FFTSignalFilterPresetManager.java created 2008-01-26
 *
 */

package org.signalml.app.config.preset.managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.filter.FFTSampleFilter;

/** FFTSignalFilterPresetManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("fftfilters")
public class FFTSampleFilterPresetManager extends AbstractPresetManager {

	private static final long serialVersionUID = 1L;

	@Override
	public String getStandardFilename() {
		return "fft-filters.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return FFTSampleFilter.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createFFTSampleFilterPresetStreamer();
		}
		return streamer;
	}

	private XStream createFFTSampleFilterPresetStreamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForFFTSampleFilter(streamer);
		streamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);

		return streamer;
	}

}
