/* RawMontageGenerator.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** RawMontageGenerator
 * Class representing generator for a raw montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("rawmontage")
public class RawMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;
	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.raw" };

        /**
         * Creates a raw Montage from given montage
         * @param montage montage to be used
         */
	@Override
	public void createMontage(Montage montage) {

		boolean oldMajorChange = montage.isMajorChange();

		try {
			montage.setMajorChange(true);

			montage.reset();

			int size = montage.getSourceChannelCount();
			for (int i=0; i<size; i++) {
				montage.addMontageChannel(i);
			}
		} finally {
			montage.setMajorChange(oldMajorChange);
		}

		montage.setMontageGenerator(this);
		montage.setChanged(false);

	}

        /**
         * Checks if montage is a valid raw montage. True for all montages
         * @param sourceMontage montage to be checked
         * @param errors Errors object used to report errors (here never)
         * @return true if montage is a valid raw montage, false otherwise (i.e. never)
         */
	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors) {
		// ok for any montage
		return true;
	}

        /**
         * Compares given object to a current object. Always true if object is of type RawMontageGenerator (all raw montage generators are equal)
         * @param obj object to be compared with a current object
         * @return true if obj is equal to a current object (is of type RawMontageGenerator), false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.getClass() == RawMontageGenerator.class);
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
