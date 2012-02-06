package org.signalml.domain.montage.generators;

import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.SourceChannel;

/**
 * This class represents a generator for a linked ears montage.
 * It creates a {@link AverageReferenceMontageGenerator average reference montage}
 * in which the average of left ear channel and right ear channel is taken as
 * a reference.
 */
public class LinkedEarsMontageGenerator extends AverageReferenceMontageGenerator {

	/**
	 * Constructor.
	 */
	public LinkedEarsMontageGenerator() {

		super(new String[] {
			SourceChannel.LEFT_EAR_CHANNEL_NAME,
			SourceChannel.RIGHT_EAR_CHANNEL_NAME});
		setName(_("Linked ears montage"));
	}

}
