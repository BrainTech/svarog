package org.signalml.domain.montage.generators;

import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;

/**
 * This class represents a generator for a right ear montage. It creates a
 * {@link SingleReferenceMontageGenerator single reference channel} with right
 * ear channel as the reference channel.
 */
public class RightEarMontageGenerator extends SingleReferenceMontageGenerator {

	/**
	 * Constructor. Creates this montage generator.
	 */
	public RightEarMontageGenerator() {
		super(SourceChannel.RIGHT_EAR_CHANNEL_NAMES[0]);
		setName(_("Right ear montage"));
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
		
		for (String label : SourceChannel.RIGHT_EAR_CHANNEL_NAMES) {
			sourceChannel = sourceMontage.getSourceChannelByLabel(label);
			if (sourceChannel != null)
			{
				sourceChannelName = label;
				break;
			}
		}
		
		if (sourceChannel == null) {
			if (errors != null) {
				for (String label : SourceChannel.RIGHT_EAR_CHANNEL_NAMES) {
					errors.addError(_R("One of required channels not identified: {0}",
						label));
				}
			}
			return false;
		}
		
		this.referenceChannelName = sourceChannelName;
		return true;
	}
}
