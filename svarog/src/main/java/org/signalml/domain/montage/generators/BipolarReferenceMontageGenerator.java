/* BipolarReferenceMontageGenerator.java created 2007-11-29
 *
 */
package org.signalml.domain.montage.generators;

import java.util.ArrayList;
import java.util.List;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.system.ChannelFunction;
import org.signalml.domain.montage.system.ChannelType;

/**
 * This class represents the generator for a bipolar montage. In bipolar montage
 * each channel (i.e., waveform) represents the difference between two adjacent
 * electrodes. The entire montage consists of a series of these channels. For
 * example, the channel "Fp1-F3" represents the difference in voltage between
 * the Fp1 electrode and the F3 electrode. The next channel in the montage,
 * "F3-C3," represents the voltage difference between F3 and C3, and so on
 * through the entire array of electrodes. (source:
 * {@code http://en.wikipedia.org/wiki/Electroencephalography})
 *
 * It generates montage of that type from given "raw" montage and checks if
 * {@link SourceMontage montages} are valid bipolar montages.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 * Sp. z o.o.
 */
public class BipolarReferenceMontageGenerator extends AbstractMontageGenerator {

	private static final long serialVersionUID = 1L;

	/**
	 * An array of pairs of channels (channels {@link Channel functions}) that
	 * will be used to create montage channels. Each pair is used to create one
	 * {@link MontageChannel montage channel}. First element as primary channel,
	 * second as reference.
	 */
	protected transient String[][] channelPairs;

	public BipolarReferenceMontageGenerator() {
	}

	/**
	 * Constructor. Creates a generator for an bipolar reference montage based
	 * on <i>refChannels</i> array
	 *
	 * @param channelPairs an array of pairs of {@link Channel channels}
	 * (channels functions) that will be used to create montage channels. Each
	 * pair is used to create one {@link MontageChannel montage channel}. First
	 * element as primary channel, second as reference.
	 */
	public BipolarReferenceMontageGenerator(String[][] channelPairs) {
		setChannelPairs(channelPairs);
	}

	public void setChannelPairs(String[][] channelPairs) {
		if (channelPairs == null || channelPairs.length == 0) {
			throw new NullPointerException("Definition cannot be null or empty");
		}
		this.channelPairs = channelPairs;
	}

	/**
	 * prepares channel pairs for the createMontage *
	 */
	protected List<List<SourceChannel>> getPrimaryAndReferenceChannels(Montage montage) throws MontageException {
		List<List<SourceChannel>> listOfLists = new ArrayList<>();
		List<SourceChannel> primaryChannels = new ArrayList<SourceChannel>();
		List<SourceChannel> referenceChannels = new ArrayList<SourceChannel>();
		for (String[] channelPair : channelPairs) {
			String channelName = channelPair[0];
			SourceChannel sourceChannel = montage.getSourceChannelByLabel(channelName);
			if (sourceChannel == null) {
				throw new MontageException(_R("Cannot find primary channel {0}", channelName));
			}
			primaryChannels.add(sourceChannel);
			channelName = channelPair[1];
			sourceChannel = montage.getSourceChannelByLabel(channelName);
			if (sourceChannel == null) {
				throw new MontageException(_R("Cannot find reference channel {0}", channelName));
			}
			referenceChannels.add(sourceChannel);
		}
		listOfLists.add(primaryChannels);
		listOfLists.add(referenceChannels);
		return listOfLists;
	}

	/**
	 * Creates a bipolar montage from the given montage.
	 *
	 * @param montage the montage to be used
	 * @throws MontageException if two channels have the same function (in the
	 * given montage) or there is no channel with some function
	 */
	@Override
	public void createMontage(Montage montage) throws MontageException {

		List<List<SourceChannel>> allChannels = getPrimaryAndReferenceChannels(montage);
		List<SourceChannel> primaryChannels = allChannels.get(0);
		List<SourceChannel> referenceChannels = allChannels.get(1);

		if (primaryChannels.isEmpty()) {
			throw new MontageException(_("Cannot find at least one fitting pair of channels"));
		}

		String token = "-1";

		boolean oldMajorChange = montage.isMajorChange();

		try {

			montage.setMajorChange(true);

			montage.reset();

			int index;
			for (int i = 0; i < primaryChannels.size(); i++) {
				int primaryChannelIndex = primaryChannels.get(i).getChannel();
				int referenceChannelIndex = referenceChannels.get(i).getChannel();
				index = montage.addMontageChannel(primaryChannelIndex);
				montage.setReference(index, referenceChannelIndex, token);
				montage.setMontageChannelLabelAt(index, montage.getSourceChannelLabelAt(primaryChannelIndex) + "-" + montage.getSourceChannelLabelAt(referenceChannelIndex));
			}

			int size = montage.getSourceChannelCount();

			for (int i = 0; i < size; i++) {
				SourceChannel sourceChannel = montage.getSourceChannelAt(i);
				if (sourceChannel.getFunction() != ChannelFunction.EEG
						|| (sourceChannel.getFunction() == ChannelFunction.EEG
						&& !sourceChannel.isChannelType(ChannelType.PRIMARY))) {
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
	 *
	 * @param sourceMontage the montage to be checked
	 * @param errors Errors object used to report errors
	 * @return true if the montage is a valid bipolar montage, false otherwise
	 */
	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, ValidationErrors errors) {

		boolean ok = true;

		for (String[] channelPair : channelPairs) {
			for (int j = 0; j < 2; j++) {
				SourceChannel sourceChannel = sourceMontage.getSourceChannelByLabel(channelPair[j]);
				if (sourceChannel == null) {
					onNotFound(channelPair[j], errors);
					ok = false;
				}
			}
		}

		return ok;

	}

}
