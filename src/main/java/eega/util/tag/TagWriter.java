package eega.util.tag;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class provides methods that write the binary tag file.
 * 
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
public class TagWriter {

	BufferedOutputStream bos;
	byte[] b = new byte[10];
	long[] l = new long[10];

	/**
	 * Creates a TagWrite writing to the given file.
	 * 
	 * @param name
	 *            the file name
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public TagWriter(String name) throws IOException {
		bos = new BufferedOutputStream(new FileOutputStream(name), 4096);
	}

	/**
	 * Writes a portion of the byte array into the file.
	 * 
	 * @param b
	 *            the byte array
	 * @param off
	 *            the offset of the first byte to write
	 * @param len
	 *            the number of bytes to write
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		bos.write(b, off, len);
	}

	/**
	 * Writes a byte to the file.
	 * 
	 * @param b
	 *            the byte to write
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void writeByte(byte b) throws IOException {
		bos.write(b);
	}

	/**
	 * Writes the two least significant bytes of the int to the file.
	 * 
	 * @param i
	 *            the int
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void write2BInt(int i) throws IOException {
		long l = i;
		b[0] = (byte) l;
		l = l >> 8;
		b[1] = (byte) l;
		bos.write(b, 0, 2);
	}

	/**
	 * Writes the four least significant bytes of the long to the file.
	 * 
	 * @param l
	 *            the long
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void write4BInt(long l) throws IOException {
		b[0] = (byte) l;
		l = l >> 8;
		b[1] = (byte) l;
		l = l >> 8;
		b[2] = (byte) l;
		l = l >> 8;
		b[3] = (byte) l;
		bos.write(b, 0, 4);
	}

	/**
	 * Closes the writer and the underlying stream.
	 */
	public void close() {
		try {
			bos.close();
		} catch (IOException ex) {
		}
	}

}
