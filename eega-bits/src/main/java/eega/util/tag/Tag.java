package eega.util.tag;

/**This class is a simple superclass of all tags (page, block & channel).
 *
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
public abstract class Tag {

	byte tag;

	/**Returns the tag code.
	 *
	 * @return the code
	 */
	public byte getTag() {
		return (tag);
	}

}
