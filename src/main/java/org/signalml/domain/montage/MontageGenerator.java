/* MontageGenerator.java created 2007-11-22
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.Errors;

/** MontageGenerator
 * Interface for all montage generators
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageGenerator extends MessageSourceResolvable, Serializable {

        /**
         * Creates a montage of specified type (depending on a type of MontageGenerator - current object) from given montage.
         * @param montage montage to be used
         * @throws MontageException thrown if error in creating montage (when it occurs depends on a type of MontageGenerator - current object)
         */
	void createMontage(Montage montage) throws MontageException;

        /**
         * Checks if montage is a valid montage of specified type (depending on a type of MontageGenerator - current object)
         * @param sourceMontage montage to be checked
         * @param errors Errors object used to report errors
         * @return true if montage is a valid montage of specified type, false otherwise
         */
	boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors);

}
