package org.signalml.domain.montage.generators;

import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.SourceChannel;

/**
 * This class represents a generator for a right ear montage.
 * It creates a {@link SingleReferenceMontageGenerator single reference channel}
 * with right ear channel as the reference channel.
 */
public class RightEarMontageGenerator extends SingleReferenceMontageGenerator {

	/**
	 * Constructor. Creates this montage generator.
	 */
	public RightEarMontageGenerator() {
		super(SourceChannel.RIGHT_EAR_CHANNEL_NAME);
		setName(_("Right ear montage"));
	}
}
