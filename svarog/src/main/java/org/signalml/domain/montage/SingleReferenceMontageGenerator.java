/* SingleChannelReferenceMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

/**
 * This abstract class represents a generator for a single reference montage.
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
public abstract class SingleReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * the {@link Channel function} of a {@link SourceChannel source channel}
         * which will be a reference channel
         */
	protected transient Channel refChannel;

        /**
         * Constructor. Creates a new generator based on {@link Channel function}
         * of a reference channel.
         * @param refChannel function of a {@link SourceChannel source channel}
         * that will be used as reference channel in a created
         * {@link Montage montage}
         */
	protected SingleReferenceMontageGenerator(Channel refChannel) {
		if (refChannel == null) {
			throw new NullPointerException("Channel cannot be null");
		}
		this.refChannel = refChannel;
	}

        /**
         * Creates a single channel reference montage from a given
         * {@link Montage montage}.
         * @param montage a montage to be used
         * @throws MontageException thrown if there are two channels with
         * function <i>refChannel</i>
         */
	@Override
	public void createMontage(Montage montage) throws MontageException {

		int[] refChannelIndices = montage.getSourceChannelsByFunction(refChannel);
		if (refChannelIndices == null || refChannelIndices.length != 1) {
			throw new MontageException("Bad refChannel count [" + refChannelIndices.length + "]");
		}

		boolean oldMajorChange = montage.isMajorChange();

		try {
			montage.setMajorChange(true);

			montage.reset();

			int size = montage.getSourceChannelCount();
			int index;
			for (int i=0; i<size; i++) {
				index = montage.addMontageChannel(i);
				if (montage.getSourceChannelFunctionAt(i).getType() == ChannelType.PRIMARY) {
					montage.setReference(index, refChannelIndices[0], "-1");
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
	public boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors) {
		int[] refChannelIndices = sourceMontage.getSourceChannelsByFunction(refChannel);
		if (refChannelIndices == null || refChannelIndices.length == 0) {
			onNotFound(errors);
			return false;
		}
		else if (refChannelIndices.length > 1) {
			onDuplicate(errors);
			return false;
		}
		return true;
	}

        /**
         * Reports error, that a {@link SourceChannel source channel}
         * with <i>refChannel</i> {@link Channel function} was not found.
         * @param errors an Errors object used to report errors
         */
	protected abstract void onNotFound(Errors errors);

        /**
         * Reports an error, that there was more then one
         * {@link SourceChannel source channel} with <i>refChannel</i>
         * {@link Channel function}.
         * @param errors an Errors object used to report errors
         */
	protected abstract void onDuplicate(Errors errors);

}
