/* LongitudinalIMontageGenerator.java created 2007-11-29
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.BipolarReferenceMontageGenerator;
import org.signalml.domain.montage.IChannelFunction;
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
	private static final IChannelFunction[][] MATRIX = new IChannelFunction[][] {

	/*{ ChannelFunction.FP1, ChannelFunction.F7 },
	{ ChannelFunction.F7, ChannelFunction.T3 },
	{ ChannelFunction.T3, ChannelFunction.T5 },
	{ ChannelFunction.T5, ChannelFunction.O1 },
	{ ChannelFunction.FP1, ChannelFunction.F3 },
	{ ChannelFunction.F3, ChannelFunction.C3 },
	{ ChannelFunction.C3, ChannelFunction.P3 },
	{ ChannelFunction.P3, ChannelFunction.O1 },
	{ ChannelFunction.FPZ, ChannelFunction.FZ },
	{ ChannelFunction.FZ, ChannelFunction.CZ },
	{ ChannelFunction.CZ, ChannelFunction.PZ },
	{ ChannelFunction.PZ, ChannelFunction.OZ },
	{ ChannelFunction.FP2, ChannelFunction.F4 },
	{ ChannelFunction.F4, ChannelFunction.C4 },
	{ ChannelFunction.C4, ChannelFunction.P4 },
	{ ChannelFunction.P4, ChannelFunction.O2 },
	{ ChannelFunction.FP2, ChannelFunction.F8 },
	{ ChannelFunction.F8, ChannelFunction.T4 },
	{ ChannelFunction.T4, ChannelFunction.T6 },
	{ ChannelFunction.T6, ChannelFunction.O2 }*/
	{ ChannelFunction.EEG, ChannelFunction.EEG }

	};

        /**
         * Constructor. Creates a generator for a Longitudinal I montage.
         */
	public LongitudinalIMontageGenerator() {
		super(MATRIX);
	}

	@Override
	protected void onDuplicate(IChannelFunction refChannel, Errors errors) {
		errors.reject("montageGenerator.error.duplicateChannel", new Object[] { refChannel }, "montageGenerator.error.duplicateChannel");
	}

	@Override
	protected void onNotFound(IChannelFunction refChannel, Errors errors) {
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
