/* SingleChannelReferenceMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

/** SingleChannelReferenceMontageGenerator
 * Abstract class representing montage generator for a single channel reference montage.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SingleReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * function of a source channel which will be a reference channel
         */
	protected transient Channel refChannel;

        /**
         * Constructor. Creates new generator based on function of reference channel
         * @param refChannel
         */
	protected SingleReferenceMontageGenerator(Channel refChannel) {
		if (refChannel == null) {
			throw new NullPointerException("Channel cannot be null");
		}
		this.refChannel = refChannel;
	}

        /**
         * Creates a single channel reference montage from given montage.
         * @param montage montage to be used
         * @throws MontageException thrown if there are two channels with function <i>refChannel</i>
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
         * Checks if montage is a valid single channel reference montage.
         * @param sourceMontage montage to be checked
         * @param errors Errors object used to report errors
         * @return true if montage is a valid single channel reference montage, false otherwise
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
         * Reports error, that source channel with <i>refChannel</i> function was not found
         * @param errors Errors object used to report errors
         */
	protected abstract void onNotFound(Errors errors);

        /**
         * Reports error, that there was more then one source channel with <i>refChannel</i> function
         * @param errors Errors object used to report errors
         */
	protected abstract void onDuplicate(Errors errors);

}
