package org.signalml.app.config.preset;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.MontageGenerators;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("monageGeneratorPresets")
public class MontageGeneratorsPresetManager extends AbstractMultifileRestoreablePresetManager {

	@Override
	public String getDirectoryName() {
		return "eegSystems/generators/";
	}

	@Override
	public String[] getDefaultFileNames() {
		return new String[] { "eeg10_10_cap33.xml", "eeg10_10_cap47.xml"};
	}

	@Override
	public Class<?> getPresetClass() {
		return MontageGenerators.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createStreamer();
		}
		return streamer;
	}

	/**
	 * Creates and returns an {@link XStreamer} used by this preset manager.
	 * @return
	 */
	protected XStream createStreamer() {
		streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(streamer, EegSystemsPresetManager.class, MontageGenerators.class, IMontageGenerator.class);
		return streamer;
	}

}
