/* LeftEarMontageGenerator.java created 2007-11-23
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
 * This class represents a generator for a left ear montage.
 * It creates a {@link SingleReferenceMontageGenerator single reference montage}
 * with EegChannel.A1 (left ear) as a {@link Channel function} of reference channel.
 *
 * @see MontageGenerator
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("leftearmontage")
public class LeftEarMontageGenerator extends SingleReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.leftEar" };

        /**
         * Constructor. Creates a generator for a left ear montage.
         */
	public LeftEarMontageGenerator() {
		super(EegChannel.A1);
	}

	/*
	private Object readResolve() {
		refChannel = EegChannel.A1;
		return this;
	}
	*/


        /**
         * Reports an error, that there was more then one
         * {@link SourceChannel source channel} with EegChannel.A1 function.
         * @param errors an Errors object used to report errors
         */
	@Override
	protected void onDuplicate(Errors errors) {
		errors.reject("montageGenerator.error.duplicateLeftEarChannel");
	}

        /**
         * Reports an error, that a {@link SourceChannel source channel} with
         * EegChannel.A1 {@link Channel function} was not found.
         * @param errors an Errors object used to report errors
         */
	@Override
	protected void onNotFound(Errors errors) {
		errors.reject("montageGenerator.error.noLeftEarChannel");
	}

        /**
         * Compares a given object to this generator. Always true if an object
         * is not null and of type LeftEarMontageGenerator
         * (all left ear montage generators are equal).
         * @param obj an object to be compared with this generator
         * @return true if obj is equal to this generator (is of type
         * LeftEarMontageGenerator), false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.getClass() == LeftEarMontageGenerator.class); // all generators of this class are equal
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
