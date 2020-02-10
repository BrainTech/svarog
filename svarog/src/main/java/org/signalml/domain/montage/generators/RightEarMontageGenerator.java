package org.signalml.domain.montage.generators;

import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;

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
		SourceChannel sourceChannelOrig = sourceMontage.getSourceChannelByLabel(SourceChannel.RIGHT_EAR_CHANNEL_NAME);
		SourceChannel sourceChannelAlt = sourceMontage.getSourceChannelByLabel(SourceChannel.RIGHT_EAR_CHANNEL_NAME_ALTERNATIVE);
		if (sourceChannelOrig == null && sourceChannelAlt == null)
		{
                        if (errors != null)
                        {
                            errors.addError(_R("One of required channels not identified: {0} or {1}",
                                    SourceChannel.RIGHT_EAR_CHANNEL_NAME,
                                    SourceChannel.RIGHT_EAR_CHANNEL_NAME_ALTERNATIVE));
                        }
			return false;
		}
		if (sourceChannelOrig == null && sourceChannelAlt != null)
		{
			this.referenceChannelName = SourceChannel.RIGHT_EAR_CHANNEL_NAME_ALTERNATIVE;
			return true;
		}
		
		if (sourceChannelOrig != null && sourceChannelAlt == null)
		{
			this.referenceChannelName = SourceChannel.RIGHT_EAR_CHANNEL_NAME;
			return true;
		}
		
		if (sourceChannelOrig != null && sourceChannelAlt != null)
		{
			this.referenceChannelName = SourceChannel.RIGHT_EAR_CHANNEL_NAME;
			return true;
		}
		
		return true;
	}
}