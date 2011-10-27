/* AverageReferenceMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage.generators;

import java.util.ArrayList;
import java.util.List;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.generators.AbstractMontageGenerator;
import org.springframework.validation.Errors;

/**
 * This abstract class represents a generator for an average reference montage.
 * In the average reference montage the outputs of all of the amplifiers are
 * summed and averaged, and this averaged signal is used as the common reference
 * for each channel.
 * (source: {@code http://en.wikipedia.org/wiki/Electroencephalography})
 *
 * This class generates montage of that type from the given "raw" montage and checks if
 * the given {@link SourceMontage montages} are valid average reference montages.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AverageReferenceMontageGenerator extends AbstractMontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * an array with functions of {@link SourceChannel source channels} that
         * this generator will use as reference channels in the created
         * {@link Montage montages} (actually in the
         * {@link MontageChannel montage channels} in a montage)
         */
	protected String[] referenceChannelsNames;

        /**
         * Constructor. Creates a generator for an average reference montage
         * based on the <i>refChannels</i> array.
         * @param refChannels array with functions of
         * {@link SourceChannel source channels} that will be used as
         * reference channels in created {@link Montage montage}
         * (actually in {@link MontageChannel montage channels} in a montage)
         * @throws NullPointerException if the array of channels is null or empty
         */
	public AverageReferenceMontageGenerator(String[] refChannelsNames) {
		if (refChannelsNames == null) {
			throw new NullPointerException("Channels cannot be null or empty");
		}
		this.referenceChannelsNames = refChannelsNames;
	}

        /**
         * Creates an average reference montage from a given montage.
         * @param sourceMontage the montage to be used
         * @throws MontageException thrown if two channels have the same function
         * or there is no channel with some function
         */
	@Override
	public void createMontage(Montage montage) throws MontageException {
		List<SourceChannel> referenceSourceChannels = getReferenceSourceChannels(montage);

		String token = "-1/" + Integer.toString(referenceSourceChannels.size());
		boolean oldMajorChange = montage.isMajorChange();

		try {
			montage.setMajorChange(true);

			montage.reset();

			int size = montage.getSourceChannelCount();
			int index;
			for (int i=0; i<size; i++) {
				index = montage.addMontageChannel(i);
				for (SourceChannel referenceSourceChannel: referenceSourceChannels) {
					if (montage.getSourceChannelAt(i).getEegElectrode() != null &&
						montage.getSourceChannelAt(i).getEegElectrode().getChannelType() == ChannelType.PRIMARY)
					montage.setReference(index, referenceSourceChannel.getChannel(), token);
				}
			}
		} finally {
			montage.setMajorChange(oldMajorChange);
		}

		montage.setMontageGenerator(this);
		montage.setChanged(false);

	}

	/**
	 * Returns the list of the {@link SourceChannel SourceChannels} from
	 * given sourceMontage that will be used as the reference channels.
	 * @param sourceMontage the source montage to be search through
	 * @return the list of reference {@link SourceChannel SourceChannels}.
	 */
	protected List<SourceChannel> getReferenceSourceChannels(SourceMontage sourceMontage) {
		List<SourceChannel> sourceChannels = new ArrayList<SourceChannel>();
		for (int i = 0; i < referenceChannelsNames.length; i++) {
			SourceChannel sourceChannel = sourceMontage.getSourceChannelByLabel(referenceChannelsNames[i]);
			if (sourceChannel != null)
				sourceChannels.add(sourceChannel);
		}
		return sourceChannels;
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

		boolean ok = true;

		List<SourceChannel> sourceChannels = new ArrayList<SourceChannel>();
		for (int i = 0; i < referenceChannelsNames.length; i++) {
			SourceChannel sourceChannel = sourceMontage.getSourceChannelByLabel(referenceChannelsNames[i]);
			if (sourceChannel != null)
				sourceChannels.add(sourceChannel);
			else {
				onNotFound(referenceChannelsNames[i], errors);
				ok = false;
			}

		}

		return ok;

	}

}
