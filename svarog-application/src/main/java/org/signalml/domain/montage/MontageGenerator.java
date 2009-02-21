/* MontageGenerator.java created 2007-11-22
 * 
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.Errors;

/** MontageGenerator
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageGenerator extends MessageSourceResolvable, Serializable {

	void createMontage( Montage montage ) throws MontageException; 
	
	boolean validateSourceMontage( SourceMontage sourceMontage, Errors errors );
	
}
