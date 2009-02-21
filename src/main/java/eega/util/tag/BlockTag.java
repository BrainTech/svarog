package eega.util.tag;

import java.io.IOException;

/**
 * This class implements a block tag. Block tags can be compared by their
 * position, so an array of block tags can be sorted along the signal.
 * 
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
@SuppressWarnings("unchecked")
public class BlockTag extends Tag implements Comparable {

	int page;
	byte block;

	BlockTag() {
	}

	/**
	 * Constructs a block tag with the given tag code, page number and block
	 * number.
	 * 
	 * @param tag
	 *            the tag code
	 * @param page
	 *            the page on which the block tag is placed
	 * @param block
	 *            the block number which is tagged
	 */
	public BlockTag(byte tag, int page, byte block) {
		this.tag = tag;
		this.page = page;
		this.block = block;
	}

	/**
	 * Returns the page of the tag.
	 * 
	 * @return the page
	 */
	public int getPage() {
		return (page);
	}

	/**
	 * Returns the block of the tag.
	 * 
	 * @return the block
	 */
	public byte getBlock() {
		return (block);
	}

	/**
	 * This method reads the values of this block tag from the given reader.
	 * 
	 * @param tr
	 *            the tag reader to read from
	 * @exception IOException
	 *                thrown if an I/O error occurs or the contents are invalid
	 */
	public void read(TagReader tr) throws IOException {
		tag = (byte) tr.readUnsignedByteAsShort();
		page = tr.readUnsignedShortAsInt();
		block = (byte) tr.readUnsignedByteAsShort();
	}

	/**
	 * This method writes the values of this block tag to the given writer.
	 * 
	 * @param tw
	 *            the tag writer to write to
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void write(TagWriter tw) throws IOException {
		tw.writeByte(tag);
		tw.write2BInt(page);
		tw.writeByte(block);
	}

	/**
	 * Compares this block tag to another block tag by time.
	 * 
	 * @param o
	 *            the block tag to compare to
	 * @return 0 if the tags are on the same block, <0 if this tag is earlier
	 *         than the given tag, and >0 if it comes later
	 */
	public int compareTo(Object o) {
		BlockTag bt = (BlockTag) o;
		int diff = page - bt.page;
		if (diff != 0)
			return (diff);
		return ((int) (block - bt.block));
	}

}
