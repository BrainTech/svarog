/* TimeDomainSampleFilterPresetManager.java created 2010-09-22
 *
 */

package org.signalml.app.config.preset.managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/** TimeDomainSampleFilterPresetManager
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("timeDomainSampleFilters")
public class TimeDomainSampleFilterPresetManager extends AbstractPresetManager {

	private static final long serialVersionUID = 1L;

	@Override
	public String getStandardFilename() {
		return "timeDomainSampleFilters.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return TimeDomainSampleFilter.class;
	}

	@Override
	public XStream getStreamer() {

		if (streamer == null)
			streamer = createTimeDomainSampleFilterPresetStreamer();
		return streamer;

	}

	private XStream createTimeDomainSampleFilterPresetStreamer() {

		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForTimeDomainSampleFilter(streamer);
		streamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);

		return streamer;

	}

}