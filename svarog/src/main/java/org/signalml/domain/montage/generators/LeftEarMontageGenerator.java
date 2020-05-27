package org.signalml.domain.montage.generators;

import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;

/**
 * This class represents a generator for a left ear montage. It creates a
 * {@link SingleReferenceMontageGenerator single reference channel} with left
 * ear channel as the reference channel.
 */
public class LeftEarMontageGenerator extends SingleReferenceMontageGenerator {

	/**
	 * Constructor. Creates the montage generator.
	 */
	public LeftEarMontageGenerator() {
		super(SourceChannel.LEFT_EAR_CHANNEL_NAMES[0]);
		setName(_("Left ear montage"));
	}

	/**
	 * Checks if {@link Montage montage} is a valid single channel reference
	 * montage.
	 *
	 * @param sourceMontage a montage to be checked
	 * @param errors an Errors object used to report errors
	 * @return true if the montage is a valid single channel reference montage,
	 * false otherwise
	 */
	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, ValidationErrors errors) {
		SourceChannel sourceChannel = null;
		String sourceChannelName = null;
		
		for (String i:SourceChannel.LEFT_EAR_CHANNEL_NAMES) {
			sourceChannel = sourceMontage.getSourceChannelByLabel(i);
			if (sourceChannel != null)
			{
				sourceChannelName = i;
				break;
			}
		}
		
		if (sourceChannel == null) {
			if (errors != null) {
				for (String i:SourceChannel.LEFT_EAR_CHANNEL_NAMES) {
					errors.addError(_R("One of required channels not identified: {0}",
						i));
				}
			}
			return false;
		}
		
		this.referenceChannelName = sourceChannelName;
		return true;
	}

}
