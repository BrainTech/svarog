/* TransverseIMontageGenerator.java created 2007-11-29
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.BipolarReferenceMontageGenerator;
import org.signalml.domain.montage.Channel;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a montage generator for a transverse I montage.
 * It creates a {@link BipolarReferenceMontageGenerator bipolar reference montage}
 * with a certain array of pairs of channels.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("transverseImontage")
public class TransverseIMontageGenerator extends BipolarReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.transverseI" };

         /**
         * An array of pairs of channels (channels functions) that will be used
         * to create montage channels.
         * Each pair is used to create one {@link MontageChannel montage channel}.
         * First element as primary channel, second as reference
         */
	private static final Channel[][] MATRIX = new Channel[][] {

	{ EegChannel.F7, EegChannel.FP1 },
	{ EegChannel.FP1, EegChannel.FP2 },
	{ EegChannel.FP2, EegChannel.F8 },
	{ EegChannel.F7, EegChannel.F3 },
	{ EegChannel.F3, EegChannel.FZ },
	{ EegChannel.FZ, EegChannel.F4 },
	{ EegChannel.F4, EegChannel.F8 },
	{ EegChannel.T3, EegChannel.C3 },
	{ EegChannel.C3, EegChannel.CZ },
	{ EegChannel.CZ, EegChannel.C4 },
	{ EegChannel.C4, EegChannel.T4 },
	{ EegChannel.T5, EegChannel.P3 },
	{ EegChannel.P3, EegChannel.PZ },
	{ EegChannel.PZ, EegChannel.P4 },
	{ EegChannel.P4, EegChannel.T6 },
	{ EegChannel.T5, EegChannel.O1 },
	{ EegChannel.O1, EegChannel.O2 },
	{ EegChannel.O2, EegChannel.T6 },

	};

        /**
         * Constructor. Creates a generator for a tranverse I montage.
         */
	public TransverseIMontageGenerator() {
		super(MATRIX);
	}

        /**
         * Reports an error, that the channel (the function of a source channel)
         * was not unique
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	@Override
	protected void onDuplicate(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.duplicateChannel", new Object[] { refChannel }, "montageGenerator.error.duplicateChannel");
	}

        /**
         * Reports an error, that the channel (the function of a source channel)
         * was not found
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	@Override
	protected void onNotFound(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.missingChannel", new Object[] { refChannel }, "montageGenerator.error.missingChannel");

	}

        /**
         * Compares a given object to a current object. Always true if an object
         * is not null and of type TransverseIMontageGenerator
         * (all left ear montage generators are equal)
         * @param obj an object to be compared with a current object
         * @return true if obj is equal to a current object (is of type
         * TransverseIMontageGenerator), false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.getClass() == TransverseIMontageGenerator.class); // all generators of this class are equal
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
