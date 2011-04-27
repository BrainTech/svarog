/* BipolarReferenceMontageGenerator.java created 2007-11-29
 *
 */

package org.signalml.domain.montage;

import java.util.HashMap;

import org.springframework.validation.Errors;

/**
 * This abstract class represents the generator for a bipolar montage.
 * In bipolar montage each channel (i.e., waveform) represents the difference
 * between two adjacent electrodes. The entire montage consists of a series
 * of these channels.
 * For example, the channel "Fp1-F3" represents the difference in voltage
 * between the Fp1 electrode and the F3 electrode. The next channel in the montage,
 * "F3-C3," represents the voltage difference between F3 and C3, and so on through
 * the entire array of electrodes.
 * (source: {@code http://en.wikipedia.org/wiki/Electroencephalography})
 * 
 * It generates montage of that type from given "raw" montage and checks
 * if {@link SourceMontage montages} are valid bipolar montages.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class BipolarReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * An array of pairs of channels (channels {@link Channel functions})
         * that will be used to create montage channels.
         * Each pair is used to create one {@link MontageChannel montage channel}.
         * First element as primary channel, second as reference.
         */
	protected transient Channel[][] definition;

        /**
         * Constructor. Creates a generator for an bipolar reference montage
         * based on <i>refChannels</i> array
         * @param definition an array of pairs of {@link Channel channels}
         * (channels functions) that will be used to create montage channels.
         * Each pair is used to create one {@link MontageChannel montage channel}.
         * First element as primary channel, second as reference.
         */
	protected BipolarReferenceMontageGenerator(Channel[][] definition) {
		if (definition == null || definition.length == 0) {
			throw new NullPointerException("Definition cannot be null or empty");
		}
		this.definition = definition;
	}

        /**
         * Creates a bipolar montage from the given montage.
         * @param montage the montage to be used
         * @throws MontageException if two channels have the same function
         * (in the given montage) or there is no channel with some function
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
         * Checks if the montage is a valid bipolar montage.
         * @param sourceMontage the montage to be checked
         * @param errors Errors object used to report errors
         * @return true if the montage is a valid bipolar montage, false otherwise
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
         * Checks it there is exactly one SourceChannel with a given function.
         * Puts pair [function of a source channel - index of a source channel]
         * in a map. If there is already a SourceChannel with a given function
         * true is returned
         * @param sourceMontage the montage that is being checked
         * @param map the HashMap in which pair [function of a source channel -
         * index of a source channel] will be put
         * @param channel the function of a SourceChannel
         * @param errors Errors object used to report errors
         * @return true if there is exactly one SourceChannel with a given
         * function or function already in the map, false otherwise
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
         * Reports an error, that the {@link Channel channel} (the function of
         * a source channel) was not found.
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	protected abstract void onNotFound(Channel refChannel, Errors errors);

        /**
         * Reports an error, that the {@link Channel channel} (the function of
         * a source channel) was not unique.
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	protected abstract void onDuplicate(Channel refChannel, Errors errors);

}
