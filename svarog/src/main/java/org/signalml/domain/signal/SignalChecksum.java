/* SignalChecksum.java created 2007-09-28
 *
 */

package org.signalml.domain.signal;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This class represents the checksum of the signal created using the
 * given {@link #method method}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("signature")
public class SignalChecksum implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * the string describing the type of this checksum, for example:
	 * 'crc32' - 32 bits cyclic redundancy check
	 */
	@XStreamAsAttribute
	private String method;

	/**
	 * the position in the signal from which calculation of this checksum
	 * starts
	 */
	@XStreamAsAttribute
	private int offset;

	/**
	 * the length of the part of the signal for which this checksum is
	 * calculated
	 */
	@XStreamAsAttribute
	private int length;

	/**
	 * the value of calculated checksum
	 */
	@XStreamAsAttribute
	private String value;

	/**
	 * Constructor. Creates the checksum of a given type for a given
	 * part of the signal and sets it to the given value
	 * @param method the string describing the type of this checksum,
	 * for example:
	 * 'crc32' - 32 bits cyclic redundancy check
	 * @param offset the position in the signal from which calculation of
	 * this checksum starts
	 * @param length the length of the part of the signal for which this
	 * checksum is calculated
	 * @param value the value of calculated checksum
	 */
	public SignalChecksum(String method, int offset, int length, String value) {
		this.method = method;
		this.offset = offset;
		this.length = length;
		this.value = value;
	}

	/**
	 * Constructor. Creates an empty checksum
	 */
	public SignalChecksum() {
	}

	/**
	 * Returns the string describing the type of this checksum, for example:
	 * 'crc32' - 32 bits cyclic redundancy check.
	 * @return the string describing the type of this checksum
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the type of this checksum, for example:
	 * 'crc32' - 32 bits cyclic redundancy check
	 * @param method the string describing the type of this checksum
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Returns the position in the signal from which calculation of
	 * this checksum starts.
	 * @return the position in the signal from which calculation of
	 * this checksum starts
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the position in the signal from which calculation of
	 * this checksum starts.
	 * @param offset the position in the signal from which calculation of
	 * this checksum starts
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Returns the length of the part of the signal for which this
	 * checksum is calculated.
	 * @return the length of the part of the signal for which this
	 * checksum is calculated
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Sets the length of the part of the signal for which this
	 * checksum is calculated.
	 * @param length the length of the part of the signal for which this
	 * checksum is calculated
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Returns the value of calculated checksum.
	 * @return string with the value of calculated checksum
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of calculated checksum
	 * @param value string with the value of calculated checksum
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
