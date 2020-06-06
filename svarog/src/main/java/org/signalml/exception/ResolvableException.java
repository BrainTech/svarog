/* ResolvableException.java created 2007-09-19
 *
 */

package org.signalml.exception;

import java.util.LinkedList;
import java.util.List;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.MessageSourceResolvable;

@Deprecated
/** ResolvableException
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResolvableException extends SignalMLException implements MessageSourceResolvable {

	private static final long serialVersionUID = 1L;

	private final String[] codes;
	private final Object[] arguments;
	private final String defaultMessage;

	public ResolvableException(String code) {
		super(code);
		this.codes = new String[] { code };
		this.arguments = new Object[0];
		this.defaultMessage = code;
	}

	public ResolvableException(String code, Object[] arguments) {
		super(code);
		this.codes = new String[] { code };
		this.arguments = arguments;
		this.defaultMessage = code;
	}

	public ResolvableException(String[] codes, Object[] arguments) {
		super((codes.length > 0 ? codes[0] : ""));
		this.codes = codes;
		this.arguments = arguments;
		this.defaultMessage = (codes.length > 0 ? codes[0] : "");
	}

	public ResolvableException(String[] codes, Object[] arguments, String defaultMessage) {
		super(defaultMessage);
		this.codes = codes;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	public ResolvableException(String code, Throwable cause) {
		super(cause);
		this.codes = new String[] { code };
		this.arguments = new Object[0];
		this.defaultMessage = code;
	}

	public ResolvableException(Throwable cause) {
		super(cause);

		if (cause instanceof MessageSourceResolvable) {
			MessageSourceResolvable resolvable = (MessageSourceResolvable) cause;
			this.codes = resolvable.getCodes();
			this.arguments = resolvable.getArguments();
			this.defaultMessage = resolvable.getDefaultMessage();
		} else {

			String message = cause.getMessage();

			List<String> codes = new LinkedList<>();
			if (message != null) {
				codes.add(cause.getMessage());
			}
			Class<?> clazz = cause.getClass();
			while (clazz != null) {
				codes.add(clazz.getName());
				clazz = clazz.getSuperclass();
			}
			codes.add(_("Exception [{0}] with message [{1}]"));
			this.codes = new String[codes.size()];
			codes.toArray(this.codes);

			this.arguments = new String[] { cause.getClass().getName(), message != null ? message : "" };

			this.defaultMessage = message != null ? message : _("Exception occured");

		}
	}

	@Override
	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public String[] getCodes() {
		return codes;
	}

	@Override
	public String getDefaultMessage() {
		return defaultMessage;
	}

}
