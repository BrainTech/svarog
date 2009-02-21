package eega.util.tag;

import java.io.IOException;

/**This class implements a tag type. The tag type serves as a definition of tags with the given code. The
 * definition consists of a descriptive string and graphical properties that can be used to display the
 * given tag. Since this code was ported from Borland Delphi (I guess...), the graphical the "style" and
 * "mode" properties must be the codes of such properties in BD. Anyone who knows BD well is welcome to
 * add to this class some public static final constants that will be compatible with BD, and facilitate
 * the use of this class.
 *
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
public class TTagHDRRec {

	/* Fill the Delphi mode & style constants here if you want ;) */

	byte tag;
	String hint;
	private int pColor;
	private byte pMode;
	private byte pStyle;
	private short pWidth;
	private int bColor;
	private byte bStyle;

	/**Creates an empty tag type.
	 */
	public TTagHDRRec() {
	}

	/**Creates a tag type with the given code and the given hint text. The graphical atributes are set
	 * to default values.
	 *
	 * @param tag the tag code
	 * @param hint a descriptive string
	 */
	public TTagHDRRec(byte tag, String hint) {
		this(tag, hint, 0x00FFFFFF, (byte) 0, 0x00FFFFFF, (byte) 4, (byte) 0, (short) 0);
	}

	/**Creates a tag type.
	 *
	 * @param tag the tag code
	 * @param hint a descriptive string
	 * @param bC background color
	 * @param bS background fill style
	 * @param pC pen color
	 * @param pM pen draw mode
	 * @param pS pen style
	 * @param pW pen width
	 */
	public TTagHDRRec(byte tag, String hint, int bC, byte bS, int pC, byte pM, byte pS, short pW) {
		this.tag = tag;
		this.hint = hint;
		pColor = pC;
		pMode = pM;
		pStyle = pS;
		pWidth = pW;
		bColor = bC;
		bStyle = bS;
	}

	/**Returns the tag code of this type.
	 *
	 * @return the code
	 */
	public byte getTag() {
		return (tag);
	}

	/**Returns the descritive string of this type.
	 *
	 * @return the hint
	 */
	public String getHint() {
		return (hint);
	}

	/**Returns the pen color.
	 *
	 * @return pen color
	 */
	public int getPenColor() {
		return pColor;
	}

	/**Returns the pen mode.
	 *
	 * @return pen mode
	 */
	public byte getPenMode() {
		return pMode;
	}

	/**Returns the pen style.
	 *
	 * @return pen style
	 */
	public byte getPenStyle() {
		return pStyle;
	}

	/**Returns the pen width.
	 *
	 * @return pen width
	 */
	public short getPenWidth() {
		return pWidth;
	}

	/**Returns the background color (the fill color) of the tag.
	 *
	 * @return the fill color
	 */
	public int getBackColor() {
		return bColor;
	}

	/**Returns the background style (the fill style) of the tag.
	 *
	 * @return the fill style
	 */
	public int getBackStyle() {
		return bStyle;
	}

	/**Reads the data of this tag type from the given reader.
	 *    
	 * @param tr the tag reader to read from
	 * @exception IOException thrown if an I/O error occurs or the contents are invalid
	 */
	public void read(TagReader tr) throws IOException {
		tag = (byte) tr.readUnsignedByteAsShort();

		byte[] b = new byte[TagDataSet.hintLength + 1];
		tr.readFully(b);
		char[] ch = new char[TagDataSet.hintLength + 1];
		int i;
		for (i = 0; i < ch.length; i++) {
			if( b[i] != 0 ) {
				ch[i] = (char) b[i];
			} else {
				break;
			}
		}
		hint = new String(ch, 1, i-1);

		bColor = (int) tr.readUnsignedIntAsLong();
		bStyle = (byte) tr.readUnsignedByteAsShort();
		pColor = (int) tr.readUnsignedIntAsLong();
		pMode = (byte) tr.readUnsignedByteAsShort();
		pStyle = (byte) tr.readUnsignedByteAsShort();
		pWidth = (short) tr.readUnsignedShortAsInt();

	}

	/**Writes the data of this tag type to the given writer.
	 *
	 * @param tw the tag writer to write to
	 * @exception IOException thrown if an I/O error occurs
	 */
	public void write(TagWriter tw) throws IOException {
		tw.writeByte(tag);
		byte[] b = new byte[TagDataSet.hintLength + 1];
		char[] c = new char[TagDataSet.hintLength + 1];
		byte len = (byte) (hint.length() > 30 ? 30 : hint.length());
		hint.getChars(0, len, c, 1);
		for (int i = 0; i < (TagDataSet.hintLength + 1); i++)
			b[i] = (byte) c[i];
		b[0] = len;
		tw.write(b, 0, TagDataSet.hintLength + 1);

		tw.write4BInt((long) bColor);
		tw.writeByte(bStyle);
		tw.write4BInt((long) pColor);
		tw.writeByte(pMode);
		tw.writeByte(pStyle);
		tw.write2BInt(pWidth);

	}
}
