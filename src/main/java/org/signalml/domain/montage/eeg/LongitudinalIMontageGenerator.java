/* LongitudinalIMontageGenerator.java created 2007-11-29
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.BipolarReferenceMontageGenerator;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.MontageChannel;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.montage.MontageGenerator;

/**
 * This class represents a generator for a longitudinal I montage.
 * It creates a {@link BipolarReferenceMontageGenerator bipolar reference montage}
 * with a certain array of pairs of channels.
 * <img src="doc-files/LongitudinalIMontage.png">
 * 
 * @see MontageGenerator
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("longitudinalImontage")
public class LongitudinalIMontageGenerator extends BipolarReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.longitudinalI" };

        /**
         * An array of pairs of channels (channels {@link Channel functions})
         * that will be used to create montage channels.
         * Each pair is used to create one {@link MontageChannel montage channel}.
         * First element as primary channel, second as reference.
         */
	private static final Channel[][] MATRIX = new Channel[][] {

	{ EegChannel.FP1, EegChannel.F7 },
	{ EegChannel.F7, EegChannel.T3 },
	{ EegChannel.T3, EegChannel.T5 },
	{ EegChannel.T5, EegChannel.O1 },
	{ EegChannel.FP1, EegChannel.F3 },
	{ EegChannel.F3, EegChannel.C3 },
	{ EegChannel.C3, EegChannel.P3 },
	{ EegChannel.P3, EegChannel.O1 },
	{ EegChannel.FPZ, EegChannel.FZ },
	{ EegChannel.FZ, EegChannel.CZ },
	{ EegChannel.CZ, EegChannel.PZ },
	{ EegChannel.PZ, EegChannel.OZ },
	{ EegChannel.FP2, EegChannel.F4 },
	{ EegChannel.F4, EegChannel.C4 },
	{ EegChannel.C4, EegChannel.P4 },
	{ EegChannel.P4, EegChannel.O2 },
	{ EegChannel.FP2, EegChannel.F8 },
	{ EegChannel.F8, EegChannel.T4 },
	{ EegChannel.T4, EegChannel.T6 },
	{ EegChannel.T6, EegChannel.O2 }

	};

        /**
         * Constructor. Creates a generator for a Longitudinal I montage.
         */
	public LongitudinalIMontageGenerator() {
		super(MATRIX);
	}

	@Override
	protected void onDuplicate(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.duplicateChannel", new Object[] { refChannel }, "montageGenerator.error.duplicateChannel");
	}

	@Override
	protected void onNotFound(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.missingChannel", new Object[] { refChannel }, "montageGenerator.error.missingChannel");

	}

        /**
         * Compares a given object to this generator. Always true if an object
         * is not null and of type LongitudinalIMontageGenerator
         * (all left ear montage generators are equal)
         * @param obj an object to be compared with this generator
         * @return true if obj is equal to this generator (is of type
         * LongitudinalIMontageGenerator), false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.getClass() == LongitudinalIMontageGenerator.class); // all generators of this class are equal
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return CODES[0];
	}

}
