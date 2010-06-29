/* LeftEarMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.SingleReferenceMontageGenerator;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** LeftEarMontageGenerator
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("leftearmontage")
public class LeftEarMontageGenerator extends SingleReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.leftEar" };

	public LeftEarMontageGenerator() {
		super(EegChannel.A1);
	}

	/*
	private Object readResolve() {
	    refChannel = EegChannel.A1;
	    return this;
	}
	*/


	@Override
	protected void onDuplicate(Errors errors) {
		errors.reject("montageGenerator.error.duplicateLeftEarChannel");
	}

	@Override
	protected void onNotFound(Errors errors) {
		errors.reject("montageGenerator.error.noLeftEarChannel");
	}

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
