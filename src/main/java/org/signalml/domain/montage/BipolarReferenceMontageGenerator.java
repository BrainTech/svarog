/* BipolarReferenceMontageGenerator.java created 2007-11-29
 *
 */

package org.signalml.domain.montage;

import java.util.HashMap;

import org.springframework.validation.Errors;

/** BipolarReferenceMontageGenerator
 * Abstract class representing generator for a bipolar montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class BipolarReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * Array of pairs of channels (channels functions) that will be used to create montage channels
         */
	protected transient Channel[][] definition;

        /**
         * Constructor
         * @param definition
         */
	protected BipolarReferenceMontageGenerator(Channel[][] definition) {
		if (definition == null || definition.length == 0) {
			throw new NullPointerException("Definition cannot be null or empty");
		}
		this.definition = definition;
	}

        /**
         * Creates a bipolar montage from given montage.
         * @param montage montage to be used
         * @throws MontageException thrown if two channels have the same function or there is no channel with some function
         */
	@Override
	public void createMontage(Montage montage) throws MontageException {

		int[] primChannelIndices = new int[definition.length];
		int[] refChannelIndices = new int[definition.length];
		int[] temp;
		int i;

		for (i=0; i<definition.length; i++) {

			temp = montage.getSourceChannelsByFunction(definition[i][0]);
			if (temp == null || temp.length != 1) {
				throw new MontageException("Bad primary channel count [" + temp.length + "]  for channel [" + definition[i][0] + "]");
			}

			primChannelIndices[i] = temp[0];

			temp = montage.getSourceChannelsByFunction(definition[i][1]);
			if (temp == null || temp.length != 1) {
				throw new MontageException("Bad reference channel count [" + temp.length + "]  for channel [" + definition[i][1] + "]");
			}

			refChannelIndices[i] = temp[0];

		}

		String token = "-1";

		boolean oldMajorChange = montage.isMajorChange();

		try {

			montage.setMajorChange(true);

			montage.reset();

			int index;
			for (i=0; i<definition.length; i++) {
				index = montage.addMontageChannel(primChannelIndices[i]);
				montage.setReference(index, refChannelIndices[i], token);
				montage.setMontageChannelLabelAt(index, montage.getSourceChannelLabelAt(primChannelIndices[i]) + "-" + montage.getSourceChannelLabelAt(refChannelIndices[i]));
			}

			int size = montage.getSourceChannelCount();

			for (i=0; i<size; i++) {
				if (montage.getSourceChannelFunctionAt(i).getType() != ChannelType.PRIMARY) {
					index = montage.addMontageChannel(i);
				}
			}

		} finally {
			montage.setMajorChange(oldMajorChange);
		}

		montage.setMontageGenerator(this);
		montage.setChanged(false);

	}

        /**
         * Checks if montage is a valid bipolar montage.
         * @param sourceMontage montage to be checked
         * @param errors Errors object used to report errors
         * @return true if montage is a valid bipolar montage, false otherwise
         */
	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors) {

		HashMap<Channel, Integer> map = new HashMap<Channel, Integer>();
		boolean ok = true;

		for (int i=0; i<definition.length; i++) {

			ok &= check(sourceMontage, map, definition[i][0], errors);
			ok &= check(sourceMontage, map, definition[i][1], errors);

		}

		return ok;

	}

        /**
         * Checks it there is exactly one SourceChannel with a given function. Puts pair [function of a source channel - index of a source channel] in a map.
         * If there is already a SourceChannel with a given function true is returned
         * @param sourceMontage montage that is being checked
         * @param map map in which pair [function of a source channel - index of a source channel] will be put
         * @param channel function of a SourceChannel
         * @param errors Errors object used to report errors
         * @return true if there is exactly one SourceChannel with a given function or function already in a map, false otherwise
         */
	private boolean check(SourceMontage sourceMontage, HashMap<Channel, Integer> map, Channel channel, Errors errors) {

		int[] channelIndices;

		if (map.get(channel) == null) {
			channelIndices = sourceMontage.getSourceChannelsByFunction(channel);
			if (channelIndices == null || channelIndices.length == 0) {
				onNotFound(channel, errors);
				map.put(channel, -1);
				return false;
			}
			else if (channelIndices.length > 1) {
				onDuplicate(channel, errors);
				map.put(channel, -1);
				return false;
			}
			map.put(channel, channelIndices[0]);
		}

		return true;
	}

        /**
         * Reports error, that channel (function of a source channel) was not found
         * @param refChannel channel that was not found
         * @param errors Errors object used to report errors
         */
	protected abstract void onNotFound(Channel refChannel, Errors errors);

        /**
         * Reports error, that channel (function of a source channel) was not found
         * @param refChannel channel that was not found
         * @param errors Errors object used to report errors
         */
	protected abstract void onDuplicate(Channel refChannel, Errors errors);

}
