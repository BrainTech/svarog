/* AverageReferenceMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

/**
 * This abstract class represents a generator for an average reference montage.
 * It generates montage of that type from the given "raw" montage and checks if
 * the given {@link SourceMontage montages} are valid average reference montages.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AverageReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * an array with functions of {@link SourceChannel source channels} that
         * this generator will use as reference channels in the created
         * {@link Montage montages} (actually in the
         * {@link MontageChannel montage channels} in a montage)
         */
	protected transient Channel[] refChannels;

        /**
         * Constructor. Creates a generator for an average reference montage
         * based on the <i>refChannels</i> array.
         * @param refChannels array with functions of
         * {@link SourceChannel source channels} that will be used as
         * reference channels in created {@link Montage montage}
         * (actually in {@link MontageChannel montage channels} in a montage)
         * @throws NullPointerException
         */
	protected AverageReferenceMontageGenerator(Channel[] refChannels) {
		if (refChannels == null || refChannels.length == 0) {
			throw new NullPointerException("Channels cannot be null or empty");
		}
		this.refChannels = refChannels;
	}

        /**
         * Creates an average reference montage from a given montage.
         * @param montage the montage to be used
         * @throws MontageException thrown if two channels have the same function
         * or there is no channel with some function
         */
	@Override
	public void createMontage(Montage montage) throws MontageException {

		int[] refChannelIndices = new int[refChannels.length];
		int[] temp;
		int i,e;

		for (i=0; i<refChannels.length; i++) {

			temp = montage.getSourceChannelsByFunction(refChannels[i]);
			if (temp == null || temp.length != 1) {
				throw new MontageException("Bad refChannel count [" + temp.length + "]  for channel [" + refChannels[i] + "]");
			}

			refChannelIndices[i] = temp[0];

		}

		String token = "-1/" + Integer.toString(refChannels.length);

		boolean oldMajorChange = montage.isMajorChange();

		try {
			montage.setMajorChange(true);

			montage.reset();

			int size = montage.getSourceChannelCount();
			int index;
			for (i=0; i<size; i++) {
				index = montage.addMontageChannel(i);
				if (montage.getSourceChannelFunctionAt(i).getType() == ChannelType.PRIMARY) {
					for (e=0; e<refChannelIndices.length; e++) {
						montage.setReference(index, refChannelIndices[e], token);
					}
				}
			}
		} finally {
			montage.setMajorChange(oldMajorChange);
		}

		montage.setMontageGenerator(this);
		montage.setChanged(false);

	}

        /**
         * Checks if the montage is a valid average reference montage.
         * @param sourceMontage the montage to be checked
         * @param errors Errors object used to report errors
         * @return true if the montage is a valid average reference montage,
         * false otherwise
         */
	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors) {

		int[] refChannelIndices;
		boolean ok = true;

		for (int i=0; i<refChannels.length; i++) {
			refChannelIndices = sourceMontage.getSourceChannelsByFunction(refChannels[i]);
			if (refChannelIndices == null || refChannelIndices.length == 0) {
				onNotFound(refChannels[i], errors);
				ok = false;
			}
			else if (refChannelIndices.length > 1) {
				onDuplicate(refChannels[i], errors);
				ok = false;
			}
		}

		return ok;

	}

        /**
         * Reports an error, that the {@link Channel channel} (the function of
         * a source channel) was not found
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	protected abstract void onNotFound(Channel refChannel, Errors errors);

        /**
         * Reports an error, that the {@link Channel channel} (the function of
         * a source channel) was not unique
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	protected abstract void onDuplicate(Channel refChannel, Errors errors);

}
