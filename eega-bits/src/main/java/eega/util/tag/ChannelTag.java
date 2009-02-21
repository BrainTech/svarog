package eega.util.tag;

import java.io.IOException;

/**
 * This class implements a channel tag. Channel tags can be compared by their
 * position, so an array of channel tags can be sorted along the signal.
 * 
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
@SuppressWarnings("unchecked")
public class ChannelTag extends Tag implements Comparable {

	long offset;
	int length;

	ChannelTag() {
	}

	/**
	 * Constructs a channel tag with the given tag code, an offset and a length.
	 * 
	 * @param tag
	 *            the tag code
	 * @param offset
	 *            the starting offset of the tag
	 * @param length
	 *            the length of the tag
	 */
	public ChannelTag(byte tag, long offset, int length) {
		this.tag = tag;
		this.offset = offset;
		this.length = length;
	}

	/**
	 * Returns the offset of this tag.
	 * 
	 * @return the offset
	 */
	public long getOffset() {
		return (offset);
	}

	/**
	 * Returns the length of this tag.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return (length);
	}

	/**
	 * This method reads the values of this channel tag from the given reader.
	 * 
	 * @param tr
	 *            the tag reader to read from
	 * @exception IOException
	 *                thrown if an I/O error occurs or the contents are invalid
	 */
	public void read(TagReader tr) throws IOException {
		tag = (byte) tr.readUnsignedByteAsShort();
		offset = tr.readUnsignedIntAsLong();
		length = tr.readUnsignedShortAsInt();
	}

	/**
	 * This method writes the values of this channel tag to the given writer.
	 * 
	 * @param tw
	 *            the tag writer to write to
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void write(TagWriter tw) throws IOException {
		tw.writeByte(tag);
		tw.write4BInt(offset);
		tw.write2BInt(length);
	}

	/**
	 * Compares this channel tag to another channel tag by time. The length is
	 * ignored.
	 * 
	 * @param o
	 *            the channel tag to compare to
	 * @return 0 if the tags have the same offset, <0 if this tag starts earlier
	 *         than the given tag, and >0 if it starts later
	 */
	public int compareTo(Object o) {
		ChannelTag ct = (ChannelTag) o;
		if (offset < ct.offset)
			return (-1);
		if (offset > ct.offset)
			return (1);

		return (0);

	}
}
