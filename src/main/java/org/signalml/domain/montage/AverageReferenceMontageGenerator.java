/* AverageReferenceMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

/** AverageReferenceMontageGenerator
 * Abstract class representing generator for a average reference montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AverageReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * array with SourceChannel's functions that generator should concern
         */
	protected transient Channel[] refChannels;

        /**
         * Constructor. Creates generator for average reference montage based on array with SourceChannel's functions that generator should concern
         * @param refChannels
         */
	protected AverageReferenceMontageGenerator(Channel[] refChannels) {
		if (refChannels == null || refChannels.length == 0) {
			throw new NullPointerException("Channels cannot be null or empty");
		}
		this.refChannels = refChannels;
	}

        /**
         * Creates a average reference montage from given montage.
         * @param montage montage to be used
         * @throws MontageException thrown if two channels have the same function or there is no channel with some function
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
         * Checks if montage is a valid average reference montage.
         * @param sourceMontage montage to be checked
         * @param errors Errors object used to report errors
         * @return true if montage is a valid average reference montage, false otherwise
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
         * Reports error, that channel (function of a source channel) was not found
         * @param refChannel channel that was not found
         * @param errors Errors object used to report errors
         */
	protected abstract void onNotFound(Channel refChannel, Errors errors);

        /**
         * Reports error, that channel (function of a source channel) was not unique
         * @param refChannel channel that was not found
         * @param errors Errors object used to report errors
         */
	protected abstract void onDuplicate(Channel refChannel, Errors errors);

}
