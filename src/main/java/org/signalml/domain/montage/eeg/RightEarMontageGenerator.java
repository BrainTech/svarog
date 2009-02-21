/* RightEarMontageGenerator.java created 2007-11-23
 * 
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.SingleReferenceMontageGenerator;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** RightEarMontageGenerator
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("rightearmontage")
public class RightEarMontageGenerator extends SingleReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.rightEar" };
	
	public RightEarMontageGenerator() {
		super(EegChannel.A2);
	}
			
	@Override
	protected void onDuplicate(Errors errors) {
		errors.reject( "montageGenerator.error.duplicateRightEarChannel" );
	}

	@Override
	protected void onNotFound(Errors errors) {
		errors.reject( "montageGenerator.error.noRightEarChannel" );
	}

	@Override
	public boolean equals(Object obj) {
		if( obj == null ) {
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
