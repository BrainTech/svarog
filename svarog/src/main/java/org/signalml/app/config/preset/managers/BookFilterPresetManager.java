/* BookFilterPresetManager.java created 2008-03-04
 *
 */

package org.signalml.app.config.preset.managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.book.filter.AtomFilterChain;

/** BookFilterPresetManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("bookfilters")
public class BookFilterPresetManager extends AbstractPresetManager {

	private static final long serialVersionUID = 1L;

	@Override
	public String getStandardFilename() {
		return "bookfilters.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return AtomFilterChain.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createBookFilterPresetStreamer();
		}
		return streamer;
	}

	private XStream createBookFilterPresetStreamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForBookFilter(streamer);
		streamer.setMode(XStream.ID_REFERENCES);

		return streamer;
	}

}
