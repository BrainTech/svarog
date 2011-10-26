package org.signalml.app.config.preset;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.io.IOException;
import org.signalml.app.config.AbstractXMLConfiguration;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.EegElectrode;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.math.geometry.Polar3dPoint;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegSystems")
public class EegSystemsPresetManager extends AbstractResourceXMLConfiguration {

	@Override
	public String getStandardFilename() {
		return "org/signalml/app/config/eeg_systems.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return EegSystem.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null)
			streamer = createStreamer();
		return streamer;
	}

	protected XStream createStreamer() {
		streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(streamer, EegSystemsPresetManager.class, EegSystem.class, EegElectrode.class, Polar3dPoint.class, ChannelType.class, IMontageGenerator.class);
		return streamer;
	}

}
