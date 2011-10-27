package org.signalml.domain.montage.generators;

import org.springframework.validation.Errors;

/**
 * An abstract version implementing the {@link IMontageGenerator} capable
 * of remembering the generator's code and with methods for
 * showing MontageGenerator's error.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractMontageGenerator implements IMontageGenerator {

	private Object[] arguments = new Object[0];
	private String[] codes = new String[1];

	@Override
	public void setCode(String code) {
		codes[0] = code;
	}

	@Override
	public String[] getCodes() {
		return codes;
	}

	@Override
	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public String getDefaultMessage() {
		return codes[0];
	}

	/**
	* Reports an error, that there was more then one
	* {@link SourceChannel source channel} with EegChannel.A2 function.
	* @param errors an Errors object used to report errors
	*/
	protected void onDuplicate(String channelName, Errors errors) {
		errors.reject("montageGenerator.error.duplicateChannel", new Object[]{channelName}, "montageGenerator.error.duplicateChannel");
	}

	/**
	* Reports an error, that a {@link SourceChannel source channel} with
	* EegChannel.A2 {@link Channel function} was not found.
	* @param errors an Errors object used to report errors
	*/
	protected void onNotFound(String channelName, Errors errors) {
		errors.reject("montageGenerator.error.missingChannel", new Object[]{channelName}, "montageGenerator.error.missingChannel");
	}

}
