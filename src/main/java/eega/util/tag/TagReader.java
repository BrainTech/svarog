package eega.util.tag;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class provides methods that read the binary tag file.
 * 
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
public class TagReader {

	BufferedInputStream bis;
	byte[] b = new byte[10];
	long[] l = new long[10];

	/**
	 * Creates a TagReader reading the given file.
	 * 
	 * @param name
	 *            the file name
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public TagReader(String name) throws IOException {
		bis = new BufferedInputStream(new FileInputStream(name), 4096);
	}

	/**
	 * Reads 16 bits treating the result as an unsigned number, and returns the
	 * result as an int.
	 * 
	 * @return a 16 bit unsigned value in an int
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public int readUnsignedShortAsInt() throws IOException {
		if (bis.read(b, 0, 2) != 2)
			throw new EOFException("eega.unexpEndOfFile");
		l[0] = b[0] >= 0 ? b[0] : (256 + b[0]);
		l[1] = b[1] >= 0 ? b[1] : (256 + b[1]);
		return ((int) ((l[1] << 8) | l[0]));
	}

	/**
	 * Reads 32 bits treating the result as an unsigned number, and returns the
	 * result as a long.
	 * 
	 * @return a 32 bit unsigned value in a long
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public long readUnsignedIntAsLong() throws IOException {
		if (bis.read(b, 0, 4) != 4)
			throw new EOFException("eega.unexpEndOfFile");
		l[0] = b[0] >= 0 ? b[0] : (256 + b[0]);
		l[1] = b[1] >= 0 ? b[1] : (256 + b[1]);
		l[2] = b[2] >= 0 ? b[2] : (256 + b[2]);
		l[3] = b[3] >= 0 ? b[3] : (256 + b[3]);
		return ((long) ((l[3] << 24) | (l[2] << 16) | (l[1] << 8) | l[0]));
	}

	/**
	 * Reads 8 bits treating the result as an unsigned number, and returns the
	 * result as a short.
	 * 
	 * @return a 8 bit unsigned value in a short
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public short readUnsignedByteAsShort() throws IOException {
		if (bis.read(b, 0, 1) != 1)
			throw new EOFException("eega.unexpEndOfFile");
		l[0] = b[0] >= 0 ? b[0] : (256 + b[0]);
		return ((short) l[0]);
	}

	/**
	 * Fills the provided array with bytes read from the file. The number of
	 * bytes that are read is equal to the length of the array.
	 * 
	 * @param bytes
	 *            an array to read bytes.length bytes into
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void readFully(byte[] bytes) throws IOException {
		if (bis.read(bytes) != bytes.length)
			throw new EOFException("eega.unexpEndOfFile");
	}

	/**
	 * Skips the given number of bytes in the file.
	 * 
	 * @param n
	 *            the number of bytes to skip
	 * @exception IOException
	 *                thrown if an I/O error occurs
	 */
	public void skipBytes(long n) throws IOException {
		if (bis.skip(n) != n)
			throw new EOFException("eega.unexpEndOfFile");
	}

	/**
	 * Closes the reader and the underlying stream.
	 */
	public void close() {
		try {
			bis.close();
		} catch (IOException ex) {
		}
	}

}
