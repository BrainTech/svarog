package org.signalml.domain.montage.generators;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.springframework.validation.Errors;

/**
 * An abstract version implementing the {@link IMontageGenerator} capable
 * of remembering the generator's code and with methods for
 * showing MontageGenerator's error.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractMontageGenerator implements IMontageGenerator {

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	* Reports an error, that a {@link SourceChannel source channel} with
	* EegChannel.A2 {@link Channel function} was not found.
	* @param errors an Errors object used to report errors
	*/
	protected void onNotFound(String channelName, Errors errors) {
		errors.reject("montageGenerator.error.missingChannel", new Object[]{channelName}, "montageGenerator.error.missingChannel");
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
