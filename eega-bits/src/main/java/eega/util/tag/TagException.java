package eega.util.tag;

/**This class implements an exception thrown by tag related methods.
 *
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
public class TagException extends Exception {

	private static final long serialVersionUID = 1L;

	/**Creates a new exception with no message.
	 */
	public TagException() {
		super();
	}

	/**Creates a new exception with the given message.
	 *
	 * @param s the message
	 */
	public TagException(String s) {
		super(s);
	}

}
