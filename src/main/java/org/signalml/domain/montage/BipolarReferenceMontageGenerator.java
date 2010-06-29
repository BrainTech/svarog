/* BipolarReferenceMontageGenerator.java created 2007-11-29
 *
 */

package org.signalml.domain.montage;

import java.util.HashMap;

import org.springframework.validation.Errors;

/** BipolarReferenceMontageGenerator
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class BipolarReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

	protected transient Channel[][] definition;

	protected BipolarReferenceMontageGenerator(Channel[][] definition) {
		if (definition == null || definition.length == 0) {
			throw new NullPointerException("Definition cannot be null or empty");
		}
		this.definition = definition;
	}

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

	protected abstract void onNotFound(Channel refChannel, Errors errors);
	protected abstract void onDuplicate(Channel refChannel, Errors errors);

}
