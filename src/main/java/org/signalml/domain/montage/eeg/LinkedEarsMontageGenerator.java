/* LinkedEarsMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.AverageReferenceMontageGenerator;
import org.signalml.domain.montage.Channel;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** LinkedEarsMontageGenerator
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("linkedearsmontage")
public class LinkedEarsMontageGenerator extends AverageReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.linkedEars" };

	public LinkedEarsMontageGenerator() {
		super(new Channel[] { EegChannel.A1, EegChannel.A2 });
	}

	/*
	private Object readResolve() {
	    refChannels = new Channel[] { EegChannel.A1, EegChannel.A2 };
	    return this;
	}
	*/

	@Override
	protected void onDuplicate(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.duplicateEarChannel", new Object[] { refChannel }, "montageGenerator.error.duplicateEarChannel");
	}

	@Override
	protected void onNotFound(Channel refChannel, Errors errors) {
		errors.reject("montageGenerator.error.missingEarChannel", new Object[] { refChannel }, "montageGenerator.error.missingEarChannel");

	}

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
