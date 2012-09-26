/* ResolvableString.java created 2007-10-14
 *
 */

package org.signalml.util;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

@Deprecated
/**
 * ResolvableString provides a simple localized text message in the form of a
 * MessageSourceResolvable.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResolvableString implements Serializable, MessageSourceResolvable {

	private static final long serialVersionUID = 1L;

	private String[] codes;
	private Object[] arguments;
	private String defaultMessage;

	/**
	 * Constructs a message with one code, no attributes and the default message equal to the code.
	 * @param code the code
	 */
	public ResolvableString(String code) {
		this.codes = new String[] { code };
		this.arguments = new Object[0];
		this.defaultMessage = code;
	}

	/**
	 * Constructs a message with one code, given attributes and the default message equal to the code.
	 * @param code the code
	 * @param arguments the arguments
	 */
	public ResolvableString(String code, Object[] arguments) {
		this.codes = new String[] { code };
		this.arguments = arguments;
		this.defaultMessage = code;
	}

	/**
	 * Constructs a message with one code, given attributes and given default message.
	 * @param code the code
	 * @param arguments the arguments
	 * @param defaultMessage the default message
	 */
	public ResolvableString(String code, Object[] arguments, String defaultMessage) {
		this.codes = new String[] { code };
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	/**
	 * Constructs the message with multiple codes, given attributes and given default message.
	 * @param codes the codes
	 * @param arguments the arguments
	 * @param defaultMessage the default message
	 */
	public ResolvableString(String[] codes, Object[] arguments, String defaultMessage) {
		this.codes = codes;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	/**
	 * Returns arguments of message, null if there are no arguments.
	 * @return arguments
	 */
	@Override
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 * Returns codes of message, null if there are no codes.
	 * @return codes
	 */
	@Override
	public String[] getCodes() {
		return codes;
	}

	/**
	 * Returns default message.
	 * @return default message
	 */
	@Override
	public String getDefaultMessage() {
		return defaultMessage;
	}

	/**
	 * Returns default message.
	 * @return default message
	 */
	@Override
	public String toString() {
		return defaultMessage;
	}

}
