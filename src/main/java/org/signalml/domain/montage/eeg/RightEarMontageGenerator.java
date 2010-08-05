/* RightEarMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.SingleReferenceMontageGenerator;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.MontageGenerator;
import org.signalml.domain.montage.SourceChannel;

/**
 * This class represents a generator for a right ear montage.
 * It creates a {@link SingleReferenceMontageGenerator single reference montage}
 * with EegChannel.A2 as a {@link Channel function} of reference channel.
 *
 * @see MontageGenerator
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("rightearmontage")
public class RightEarMontageGenerator extends SingleReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.rightEar" };

        /**
         * Constructor. Creates a generator for a right ear montage.
         */
	public RightEarMontageGenerator() {
		super(EegChannel.A2);
	}

        /**
         * Reports an error, that there was more then one
         * {@link SourceChannel source channel} with EegChannel.A2 function.
         * @param errors an Errors object used to report errors
         */
	@Override
	protected void onDuplicate(Errors errors) {
		errors.reject("montageGenerator.error.duplicateRightEarChannel");
	}

        /**
         * Reports an error, that a {@link SourceChannel source channel} with
         * EegChannel.A2 {@link Channel function} was not found.
         * @param errors an Errors object used to report errors
         */
	@Override
	protected void onNotFound(Errors errors) {
		errors.reject("montageGenerator.error.noRightEarChannel");
	}

        /**
         * Compares a given object to this generator. Always true if an object
         * is not null and of type RightEarMontageGenerator
         * (all left ear montage generators are equal).
         * @param obj an object to be compared with this generator
         * @return true if obj is equal to this generator (is of type
         * RightEarMontageGenerator), false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.getClass() == RightEarMontageGenerator.class); // all generators of this class are equal
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
