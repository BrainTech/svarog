package org.signalml.domain.montage.generators;

import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.SourceChannel;

/**
 * This class represents a generator for a left ear montage.
 * It creates a {@link SingleReferenceMontageGenerator single reference channel}
 * with left ear channel as the reference channel.
 */
public class LeftEarMontageGenerator extends SingleReferenceMontageGenerator {

	/**
	 * Constructor. Creates the montage generator.
	 */
	public LeftEarMontageGenerator() {
		super(SourceChannel.LEFT_EAR_CHANNEL_NAME);
		setName(_("Left ear montage"));
	}

}
