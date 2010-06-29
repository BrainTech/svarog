/* TransverseIMontageGenerator.java created 2007-11-29
 *
 */

package org.signalml.domain.montage.eeg;

import org.signalml.domain.montage.BipolarReferenceMontageGenerator;
import org.signalml.domain.montage.Channel;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** TransverseIMontageGenerator
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("transverseImontage")
public class TransverseIMontageGenerator extends BipolarReferenceMontageGenerator {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.transverseI" };

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

	public TransverseIMontageGenerator() {
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
