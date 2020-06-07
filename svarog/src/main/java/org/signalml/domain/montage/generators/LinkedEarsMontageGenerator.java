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
			SourceChannel.LEFT_EAR_CHANNEL_NAMES[0],
			SourceChannel.RIGHT_EAR_CHANNEL_NAMES[0]
		});
		setName(_("Linked ears montage"));
	}

	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, ValidationErrors errors) {
		boolean setAvailable = super.validateSourceMontage(sourceMontage, null);
		
		for (int i=0;i<SourceChannel.LEFT_EAR_CHANNEL_NAMES.length;i++){
			this.referenceChannelsNames = new String[]{
				SourceChannel.LEFT_EAR_CHANNEL_NAMES[i],
				SourceChannel.RIGHT_EAR_CHANNEL_NAMES[i]
			};
			setAvailable = super.validateSourceMontage(sourceMontage, null);
			if (setAvailable){
				return true;
			}
				
			
		}
		
		if (!setAvailable){
			if (errors != null) {
				
				for (int i=0;i<SourceChannel.LEFT_EAR_CHANNEL_NAMES.length;i++){
					errors.addError(_R("Missing ears pair: {0}, {1}.",
						SourceChannel.LEFT_EAR_CHANNEL_NAMES[i],
						SourceChannel.RIGHT_EAR_CHANNEL_NAMES[i]
				));
				}
			}
			return false;
		}
		return false;
	}
}
