/* SingleChannelReferenceMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage.generators;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.system.ChannelFunction;
import org.springframework.validation.Errors;

/**
 * This class represents a generator for a single reference montage.
 * In single reference montage each channel represents the difference between
 * a certain electrode and a designated reference electrode.
 * There is no standard position for this reference; it is, however, at a different
 * position than the "recording" electrodes. Midline positions are often used because
 * they do not amplify the signal in one hemisphere vs. the other.
 * Another popular reference is "linked ears," which is a physical or mathematical
 * average of electrodes attached to both earlobes or mastoids.
 * (source: {@code http://en.wikipedia.org/wiki/Electroencephalography})
 * 
 * This class generates montage of that type from the given "raw" montage and checks if
 * the given {@link SourceMontage montages} are valid single reference montages.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SingleReferenceMontageGenerator extends AbstractMontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * the label of a {@link SourceChannel source channel}
         * which will be a reference channel
         */
	protected transient String referenceChannelName;

        /**
         * Constructor. Creates a new generator based on label
         * of a reference channel.
         * @param refChannel label of a {@link SourceChannel source channel}
         * that will be used as reference channel in a created
         * {@link Montage montage}
         */
	public SingleReferenceMontageGenerator(String referenceChannelName) {
		if (referenceChannelName == null) {
			throw new NullPointerException("Channel cannot be null");
		}
		this.referenceChannelName = referenceChannelName;
	}

        /**
         * Creates a single channel reference montage from a given
         * {@link Montage montage}.
         * @param montage a montage to be used
         * @throws MontageException thrown if there is no channel having the
	 * label equal to the reference channel label
         */
	@Override
	public void createMontage(Montage montage) throws MontageException {

		SourceChannel referenceSourceChannel = montage.getSourceChannelByLabel(referenceChannelName);

		if (referenceSourceChannel == null) {
			throw new MontageException("Cannot find " + referenceChannelName + " source channel for single reference montage generator ");
		}

		boolean oldMajorChange = montage.isMajorChange();

		try {
			montage.setMajorChange(true);

			montage.reset();

			int size = montage.getSourceChannelCount();
			int index;
			for (int i=0; i<size; i++) {
				index = montage.addMontageChannel(i);
				SourceChannel sourceChannel = montage.getSourceChannelAt(i);

				if (sourceChannel.getFunction() == ChannelFunction.EEG
					&& !sourceChannel.isChannelType(ChannelType.REFERENCE)) {
					montage.setReference(index, referenceSourceChannel.getChannel(), "-1");
				}
			}
		} finally {
			montage.setMajorChange(oldMajorChange);
		}

		montage.setMontageGenerator(this);
		montage.setChanged(false);

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
		SourceChannel sourceChannel = sourceMontage.getSourceChannelByLabel(referenceChannelName);

		if (sourceChannel == null) {
			onNotFound(referenceChannelName, errors);
			return false;
		}
		return true;
	}

}
