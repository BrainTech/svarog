/* CAEarsMontageGenerator.java created 2011-06-23
 *
 */

package org.signalml.domain.montage.generators;

import static org.signalml.app.util.i18n.SvarogI18n._;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.system.ChannelFunction;

/**
 * This class represents a generator for a common average montage.
 * It creates a {@link AverageReferenceMontageGenerator average reference montage}
 * with all PRIMARY channels (those from 10_20 system) as functions of
 * reference channels (average of this channels is taken as reference).
 *
 * @see MontageGenerator
 * @author Mateusz Kruszyński &copy; 2011 CC Titanis
 */
@XStreamAlias("camontage")
public class CommonAverageMontageGenerator extends AverageReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

        /**
         * Constructor.
         */
	public CommonAverageMontageGenerator() {
		super(new String[0]);
		setName(_("Common average montage"));
	}

        /**
         * Creates an common average reference montage from a given montage.
         * @param montage the montage to be used
         * @throws MontageException it is never thrown for this montage generator
         */
	@Override
	public void createMontage(Montage montage) throws MontageException {
		int[] refChannelIndices = null;

		refChannelIndices = montage.getSourceChannelsByFunction(ChannelFunction.EEG);
		String token = "-1/" + Integer.toString(refChannelIndices.length-1);
		boolean oldMajorChange = montage.isMajorChange();
		try {
			montage.setMajorChange(true);

			montage.reset();

			int size = montage.getSourceChannelCount();
			int index;

			for (int i=0; i< size; i++) {
				index = montage.addMontageChannel(i);

				if (montage.getSourceChannelAt(i).getFunction() == ChannelFunction.EEG) {
					for (int e=0; e<refChannelIndices.length; e++) {
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
         * Compares a given object to this generator. Always true if an object
         * is not null and of type CAMontageGenerator
         * (all left ear montage generators are equal).
         * @param obj an object to be compared with a current object
         * @return true if obj is equal to a current object (is of type
         * CAMontageGenerator), false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.getClass() == CommonAverageMontageGenerator.class); // all generators of this class are equal
	}

}