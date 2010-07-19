/* MontageGenerator.java created 2007-11-22
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.Errors;

/**
 * Interface for all montage generators. Allows to create and validate a montage.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageGenerator extends MessageSourceResolvable, Serializable {

        /**
         * Creates a montage of a specified type (depending on a type of
         * a MontageGenerator - the current object) from a given montage.
         * @param montage a montage to be used
         * @throws MontageException thrown if there is an error in creating
         * a montage (when it occurs depends on a type of a MontageGenerator
         * - the current object)
         */
	void createMontage(Montage montage) throws MontageException;

        /**
         * Checks if a montage is a valid montage of specified type (depending
         * on a type of a MontageGenerator - the current object)
         * @param sourceMontage montage to be checked
         * @param errors Errors object used to report errors
         * @return true if a montage is a valid montage of a specified type,
         * false otherwise
         */
	boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors);

}
