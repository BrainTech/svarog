/* CAEarsMontageGenerator.java created 2011-23-06
 *
 */


package org.signalml.domain.montage.eeg;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.AverageReferenceMontageGenerator;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.ChannelType;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.montage.MontageGenerator;

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
public class CAMontageGenerator extends AverageReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.ca" };

        /**
         * Constructor.
         */
	public CAMontageGenerator() {
		super(new Channel[] {});
	}

        /**
         * Creates an common average reference montage from a given montage.
         * @param montage the montage to be used
         * @throws MontageException thrown if two channels have the same function
         * or there is no channel with some function
         */
	public void createMontage(Montage montage) throws MontageException {
		int[] refChannelIndices = null;
		refChannelIndices = montage.getSourceChannelsByTypes(new ChannelType[] {ChannelType.PRIMARY, ChannelType.REFERENCE});
		String token = "-1/" + Integer.toString(refChannelIndices.length-1);
		boolean oldMajorChange = montage.isMajorChange();
		try {
			montage.setMajorChange(true);

			montage.reset();

			int size = montage.getSourceChannelCount();
			int index;
			for (int i=0; i<size; i++) {
				index = montage.addMontageChannel(i);
				ChannelType t = montage.getSourceChannelFunctionAt(i).getType();
				if ((t == ChannelType.PRIMARY) || (t == ChannelType.REFERENCE)) {
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
         * Reports error, that the ear channel (the function of a source channel)
         * was not unique.
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	@Override
	protected void onDuplicate(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.duplicateEarChannel", new Object[] { refChannel }, "montageGenerator.error.duplicateEarChannel");
	}

        /**
         * Reports error, that the ear channel (the function of a source channel)
         * was not found.
         * @param refChannel the channel that was not found
         * @param errors the Errors object used to report errors
         */
	@Override
	protected void onNotFound(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.missingEarChannel", new Object[] { refChannel }, "montageGenerator.error.missingEarChannel");

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
		return (obj.getClass() == CAMontageGenerator.class); // all generators of this class are equal
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
