/* MontageGenerator.java created 2007-11-22
 *
 */

package org.signalml.domain.montage.generators;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceMontage;

/**
 * This interface for all montage generators allows to create and
 * validate a {@link Montage montage} of a certain type (defined by implementation).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("montageGenerator")
public interface IMontageGenerator extends Serializable {

	/**
	 * Creates a {@link Montage montage} of a specified type
	 * (depending on a type of this generator) from a given montage.
	 * @param montage a montage to be used
	 * @throws MontageException if there is an error while creating
	 * a montage (when it occurs depends on a type of this montage generator)
	 */
	void createMontage(Montage montage) throws MontageException;

	/**
	 * Checks if a {@link Montage montage} is a valid montage of a
	 * specified type (depending on a type of this generator).
	 * @param sourceMontage the montage to be checked
	 * @param errors Errors object used to report errors
	 * @return true if a montage is a valid montage of a specified type,
	 * false otherwise
	 */
	boolean validateSourceMontage(SourceMontage sourceMontage, ValidationErrors errors);

	/**
	 * Sets the name of this montage generator.
	 * @param name to be used
	 */
	void setName(String name);

	/**
	 * Returns the (display) name of this montage generator.
	 * @return the name of this montage generator.
	 */
	String getName();

}
