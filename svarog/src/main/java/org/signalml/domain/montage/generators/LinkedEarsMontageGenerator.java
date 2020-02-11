package org.signalml.domain.montage.generators;

import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;

/**
 * This class represents a generator for a linked ears montage. It creates a
 * {@link AverageReferenceMontageGenerator average reference montage} in which
 * the average of left ear channel and right ear channel is taken as a
 * reference.
 */
public class LinkedEarsMontageGenerator extends AverageReferenceMontageGenerator {

	/**
	 * Constructor.
	 */
	public LinkedEarsMontageGenerator() {

		super(new String[]{
			SourceChannel.LEFT_EAR_CHANNEL_NAME,
			SourceChannel.RIGHT_EAR_CHANNEL_NAME
		});
		setName(_("Linked ears montage"));
	}

	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, ValidationErrors errors) {
		boolean setAvailable = super.validateSourceMontage(sourceMontage, null);
		if (!setAvailable) {
			this.referenceChannelsNames = new String[]{
				SourceChannel.LEFT_EAR_CHANNEL_NAME_ALTERNATIVE,
				SourceChannel.RIGHT_EAR_CHANNEL_NAME_ALTERNATIVE
			};
			setAvailable = super.validateSourceMontage(sourceMontage, null);
		}
		if (!setAvailable) {
			this.referenceChannelsNames = new String[]{
				SourceChannel.LEFT_EAR_CHANNEL_NAME,
				SourceChannel.RIGHT_EAR_CHANNEL_NAME
			};
			if (errors != null) {
				errors.addError(_R("At least one set of ears should be available: {0}, {1} or {2}, {3}.",
						SourceChannel.LEFT_EAR_CHANNEL_NAME,
						SourceChannel.RIGHT_EAR_CHANNEL_NAME,
						SourceChannel.LEFT_EAR_CHANNEL_NAME_ALTERNATIVE,
						SourceChannel.RIGHT_EAR_CHANNEL_NAME_ALTERNATIVE
				));
			};
			return false;
		}
		return true;
	}
}
