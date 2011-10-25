package org.signalml.domain.montage.generators;

import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceMontage;
import org.springframework.validation.Errors;

/**
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

	protected void onDuplicate(String channelName, Errors errors) {
		errors.reject("montageGenerator.error.duplicateChannel", new Object[]{channelName}, "montageGenerator.error.duplicateChannel");
	}

	protected void onNotFound(String channelName, Errors errors) {
		errors.reject("montageGenerator.error.missingChannel", new Object[]{channelName}, "montageGenerator.error.missingChannel");
	}

}
