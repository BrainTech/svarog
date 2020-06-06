package org.signalml.domain.montage.generators;

import java.util.ArrayList;
import java.util.List;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;

public class LongitudalReferenceMontageGenerator extends BipolarReferenceMontageGenerator {

	String[][] PAIRS = {
	{"Fp2", "F8"},
	{"F8", "T4"},
	{"T4", "T6"},
	{"T6", "O2"},
	{"T4", "T8"},
	{"T8", "O2"},

	{"Fp1", "F7"},
	{"F7", "T3"},
	{"T3", "T5"},
	{"T5", "O1"},

	{"Fp2", "F4"},
	{"F4", "C4"},
	{"C4", "P4"},
	{"P4", "O2"},

	{"Fp1", "F3"},
	{"F3", "C3"},
	{"C3", "P3"},
	{"P3", "O1"},
	{"T3", "T7"},
	{"T7", "O1"},

	{"Fz", "Cz"},
	{"Cz", "Pz"}
	};

	public LongitudalReferenceMontageGenerator() {
		super();
		setName(_("Longitudinal Banana"));
		setChannelPairs(PAIRS);
	}

	// at least one pair exists
	public boolean validateSourceMontage(SourceMontage sourceMontage, ValidationErrors errors) {

		for (int i = 0; i < channelPairs.length; i++) {
			int pair_ok = 0;
			for (int j = 0; j < 2; j++) {
				SourceChannel sourceChannel = sourceMontage.getSourceChannelByLabel(channelPairs[i][j]);
				if (sourceChannel == null) {
					sourceChannel = sourceMontage.getSourceChannelByLabel("EEG " + channelPairs[i][j]);
				}
				if (sourceChannel != null) {
					pair_ok += 1;
				}
			}
			if (pair_ok == 2) {
				return true;
			}

		}
		return false;
	}

	/**
	 * prepares channel pairs for the createMontage *
	 */
	protected List<List<SourceChannel>> getPrimaryAndReferenceChannels(Montage montage) throws MontageException {
		List<List<SourceChannel>> listOfLists = new ArrayList<>();
		List<SourceChannel> primaryChannels = new ArrayList<SourceChannel>();
		List<SourceChannel> referenceChannels = new ArrayList<SourceChannel>();
		for (int i = 0; i < channelPairs.length; i++) {
			String channelName = channelPairs[i][0];
			SourceChannel sourceChannel = montage.getSourceChannelByLabel(channelName);
			if (sourceChannel == null) {
				sourceChannel = montage.getSourceChannelByLabel("EEG " + channelName);
			}
			if (sourceChannel == null) {
				continue;
			}

			channelName = channelPairs[i][1];
			SourceChannel referenceChannel = montage.getSourceChannelByLabel(channelName);
			if (referenceChannel == null) {
				referenceChannel = montage.getSourceChannelByLabel("EEG " + channelName);
			}
			if (referenceChannel == null) {
				continue;
			}

			primaryChannels.add(sourceChannel);
			referenceChannels.add(referenceChannel);
		}
		listOfLists.add(primaryChannels);
		listOfLists.add(referenceChannels);
		return listOfLists;
	}

}
