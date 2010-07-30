/* RawSignalSampleType.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.raw;

import org.springframework.context.MessageSourceResolvable;

/**
 * This class represents the type (actually length in bits and the
 * interpretation of these bits) of samples in the file with raw signal.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum RawSignalSampleType implements MessageSourceResolvable {

        /**
         * Samples of the length of 64 bits in the form of double
         */
	DOUBLE(64),
        /**
         * Samples of the length of 32 bits in the form of floats
         */
	FLOAT(32),
        /**
         * Samples of the length of 32 bits in the form of integers
         */
	INT(32),
        /**
         * Samples of the length of 16 bits in the form of integers
         */
	SHORT(16)

	;

        /**
         * length of a sample in bits
         */
	private int bitWidth;

        /**
         * Constructor. Creates the type of the raw signal sample using the
         * given length of the sample
         * @param bitWidth length (in bits) of the sample
         */
	private RawSignalSampleType(int bitWidth) {
		this.bitWidth = bitWidth;
	}

        /**
         * Returns the size of the sample (in bits)
         * @return the size of the sample (in bits)
         */
	public int getBitWidth() {
		return bitWidth;
	}

        /**
         * Returns the size of the sample (in standard bytes)
         * @return the size of the sample (in standard bytes)
         */
	public int getByteWidth() {
		return (int) Math.ceil(((double) bitWidth) / 8);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "sampleType." + name() };
	}

	@Override
	public String getDefaultMessage() {
		return name();
	}

}
