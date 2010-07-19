/* LinkedEarsMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.AverageReferenceMontageGenerator;
import org.signalml.domain.montage.Channel;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a montage generator for a linked ears montage.
 * It creates a {@link AverageReferenceMontageGenerator average reference montage}
 * with EegChannel.A1 and EegChannel.A2 as functions of reference channels.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("linkedearsmontage")
public class LinkedEarsMontageGenerator extends AverageReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.linkedEars" };

        /**
         * Constructor.
         */
	public LinkedEarsMontageGenerator() {
		super(new Channel[] { EegChannel.A1, EegChannel.A2 });
	}

	/*
	private Object readResolve() {
	    refChannels = new Channel[] { EegChannel.A1, EegChannel.A2 };
	    return this;
	}
	*/

         /**
         * Reports error, that the ear channel (the function of a source channel)
         * was not unique
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	@Override
	protected void onDuplicate(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.duplicateEarChannel", new Object[] { refChannel }, "montageGenerator.error.duplicateEarChannel");
	}

        /**
         * Reports error, that the ear channel (the function of a source channel)
         * was not found
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	@Override
	protected void onNotFound(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.missingEarChannel", new Object[] { refChannel }, "montageGenerator.error.missingEarChannel");

	}

        /**
         * Compares a given object to a current object. Always true if an object
         * is not null and of type LinkedEarsMontageGenerator
         * (all left ear montage generators are equal)
         * @param obj an object to be compared with a current object
         * @return true if obj is equal to a current object (is of type
         * LinkedEarsMontageGenerator), false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.getClass() == LinkedEarsMontageGenerator.class); // all generators of this class are equal
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
