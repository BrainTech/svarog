package org.signalml.domain.montage.generators;

import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;

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
	
	/**
	 * Checks if {@link Montage montage} is a valid single channel
	 * reference montage.
	 * @param sourceMontage a montage to be checked
	 * @param errors an Errors object used to report errors
	 * @return true if the montage is a valid single channel reference montage,
	 * false otherwise
	 */
	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, ValidationErrors errors) {
		SourceChannel sourceChannelOrig = sourceMontage.getSourceChannelByLabel(SourceChannel.LEFT_EAR_CHANNEL_NAME);
		SourceChannel sourceChannelAlt = sourceMontage.getSourceChannelByLabel(SourceChannel.LEFT_EAR_CHANNEL_NAME_ALTERNATIVE);
		if (sourceChannelOrig == null && sourceChannelAlt == null)
		{
                        if (errors != null)
                        {
                            errors.addError(_R("One of required channels not identified: {0} or {1}",
                                    SourceChannel.LEFT_EAR_CHANNEL_NAME,
                                    SourceChannel.LEFT_EAR_CHANNEL_NAME_ALTERNATIVE));
                        }
			return false;
		}
		if (sourceChannelOrig == null && sourceChannelAlt != null)
		{
			this.referenceChannelName = SourceChannel.LEFT_EAR_CHANNEL_NAME_ALTERNATIVE;
			return true;
		}
		
		if (sourceChannelOrig != null && sourceChannelAlt == null)
		{
			this.referenceChannelName = SourceChannel.LEFT_EAR_CHANNEL_NAME;
			return true;
		}
		
		if (sourceChannelOrig != null && sourceChannelAlt != null)
		{
			this.referenceChannelName = SourceChannel.LEFT_EAR_CHANNEL_NAME;
			return true;
		}
		return true;
	}

}
