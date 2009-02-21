package eega.util.tag;

import java.io.IOException;

/**This class implements a page tag. Page tags can be compared by their position, so an array of page
 * tags can be sorted along the signal.
 *
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
@SuppressWarnings("unchecked")
public class PageTag extends Tag implements Comparable {

	int page;

	PageTag() {
	}

	/**Constructs a page tag with the given tag code and page number.
	 *
	 * @param tag the tag code
	 * @param page the page number
	 */
	public PageTag(byte tag, int page) {
		this.tag = tag;
		this.page = page;
	}

	/**Returns the page number of this tag.
	 *
	 * @return the page number
	 */
	public int getPage() {
		return (page);
	}

	/**This method reads the values of this page tag from the given reader.
	 *
	 * @param tr the tag reader to read from
	 * @exception IOException thrown if an I/O error occurs or the contents are invalid
	 */
	public void read(TagReader tr) throws IOException {
		tag = (byte) tr.readUnsignedByteAsShort();
		page = tr.readUnsignedShortAsInt();
	}

	/**This method writes the values of this page tag to the given writer.
	 *
	 * @param tw the tag writer to write to
	 * @exception IOException thrown if an I/O error occurs
	 */
	public void write(TagWriter tw) throws IOException {
		tw.writeByte(tag);
		tw.write2BInt(page);
	}

	/**Compares this page tag to another page tag by time.
	 *
	 * @param o the page tag to compare to
	 * @return 0 if the tags are on the same page, <0 if this tag is earlier than the given tag, and
	 * >0 if it comes later
	 */
	public int compareTo(Object o) {
		PageTag pt = (PageTag) o;
		return (page - pt.page);
	}

}
