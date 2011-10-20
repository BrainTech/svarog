/* EegChannel.java created 2007-10-20
 *
 */

package org.signalml.domain.montage.eeg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.signalml.domain.montage.IChannelFunction;
import org.signalml.domain.montage.ChannelType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents an eeg channel.
 * Contains a static matrix 6x7 in which channels are held.
 * Allows to find channels by name and location and
 * to find neighbours for a given channel.
 * @see Channel
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("eegchannel")
public enum ChannelFunction implements IChannelFunction {

	UNKNOWN("Unknown", ChannelType.UNKNOWN, false, null),
	EEG("EEG", ChannelType.PRIMARY, false, null),
	REFERENCE("Reference", ChannelType.REFERENCE, false, null),
	ECG("ECG", ChannelType.OTHER, true, "[Ee][CcKk][Gg]"),
	EMG("EMG", ChannelType.OTHER, true, "[Ee][Mm][Gg]"),
	RESP("RESP", ChannelType.OTHER, true, "[Rr][Ee][Ss][Pp]"),
	SAO2("SaO2", ChannelType.OTHER, true, "[Ss][Aa]\\s*[Oo]2"),
	ZERO("ZERO", ChannelType.ZERO, true, false, "ZERO"),
	ONE("ONE", ChannelType.ONE, true, false, "ONE")
	;

        /**
         * a name of this channel
         */
	private String name;

        /**
         * a {@link ChannelType type} of this channel. Possible types:
         * UNKNOWN, PRIMARY, REFERENCE, OTHER
         */
	private ChannelType type;

        /**
         * the pattern which will be used to search this channel by name
         */
	private Pattern matchingPattern;

        /**
         * a variable telling if this channel is unique
         */
	private boolean unique;
    /**
     * a variable telling if this channel is mutable
     */
	private boolean mutable;



        /**
         * the number of a column in which this channel is located
         */
	private int matrixCol;

        /**
         * the number of a row in which this channel is located
         */
	private int matrixRow;

        /**
         * the width of channels matrix
         */
	public static final int MATRIX_WIDTH = 7;

        /**
         * the height of channels matrix
         */
	public static final int MATRIX_HEIGHT = 6;

        /**
         * the static matrix of channels
         */
	private static ChannelFunction[][] matrix;

        /**
         * Creates channels matrix.
         */
	static {

		matrix = new ChannelFunction[MATRIX_WIDTH][MATRIX_HEIGHT];
		ChannelFunction[] all = values();
		for (ChannelFunction c : all) {
			if (c.matrixCol >= 0 && c.matrixRow >= 0) {
				matrix[c.matrixCol][c.matrixRow] = c;
			}
		}

	}

        /**
         * Constructor. Creates a channel of a given {@link ChannelType type}
         * and puts it at given location.
         * @param name the name of the channel
         * @param type the type of the channel
         * @param unique is this channel unique?
         * @param pattern the regular expression which will be used to search
         * this channel by name
         * @param matrixCol the column of the matrix in which this channel
         * is located
         * @param matrixRow the row of the matrix in which this channel
         * is located
         */
	private ChannelFunction(String name, ChannelType type, boolean unique, String pattern, int matrixCol, int matrixRow) {
		this.mutable=true;
		this.name = name;
		this.type = type;
		this.unique = unique;
		if (pattern != null) {
			this.matchingPattern = Pattern.compile(pattern);
		}
		this.matrixCol = matrixCol;
		this.matrixRow = matrixRow;
	}

        /**
         * Constructor. Creates a channel of a given type and puts outside of
         * the matrix.
         * @param name the name of the channel
         * @param type the type of the channel
         * @param unique is the channel unique?
         * @param pattern the regular expression which will be used to search channel by name
         */
	private ChannelFunction(String name, ChannelType type, boolean unique, String pattern) {
		this.name = name;
		this.type = type;
		this.unique = unique;
		if (pattern != null) {
			this.matchingPattern = Pattern.compile(pattern);
		}
		this.matrixCol = -1;
		this.matrixRow = -1;
	}
    /**
     * Constructor.
     * @param name the name of the channel
     * @param type the type of the channel
     * @param unique is the channel unique?
     * @param unique is the channel mutable?
     * @param pattern the regular expression which will be used to search
     * channel by name
     */
	private ChannelFunction(String name, ChannelType type, boolean unique, boolean mutable, String pattern) {
		this(name, type, unique, pattern);
		this.mutable=mutable;
	}

        /**
         * Finds an EegChannel of a given name.
         * @param name the name of a channel to be found
         * @return an EegChannel of a given name
         */
	public static ChannelFunction forName(String name) {
		ChannelFunction[] values = values();
		Matcher matcher;
		for (ChannelFunction channel : values) {
			if (channel.matchingPattern != null) {
				matcher = channel.matchingPattern.matcher(name);
				if (matcher.matches()) {
					return channel;
				}
			}
		}
		return null;
	}

    @Override
	public String getName() {
		return name;
	}

    @Override
	public ChannelType getType() {
		return type;
	}

	@Override
	public boolean isUnique() {
		return unique;
	}

        /**
         * Returns the pattern which is used to search this channel by name.
         * @return the pattern which is used to search this channel by name
         */
	public Pattern getMatchingPattern() {
		return matchingPattern;
	}

	@Override
	public int getMatrixCol() {
		return matrixCol;
	}

	@Override
	public int getMatrixRow() {
		return matrixRow;
	}

        /**
         * Returns the matrix of channels.
         * @return the matrix of channels
         */
	public static IChannelFunction[][] getMatrix() {
		return matrix;
	}

        @Override
	public IChannelFunction getLeftNeighbour(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixCol <= 0) {
			return null;
		}
		return matrix[channel.matrixCol-1][channel.matrixRow];
	}

        @Override
	public IChannelFunction getRightNeighbour(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixCol >= MATRIX_WIDTH-1) {
			return null;
		}
		return matrix[channel.matrixCol+1][channel.matrixRow];
	}

        @Override
	public IChannelFunction getTopNeighbour(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixRow <= 0) {
			return null;
		}
		return matrix[channel.matrixCol][channel.matrixRow-1];
	}

        @Override
	public IChannelFunction getBottomNeighbour(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixRow >= MATRIX_HEIGHT-1) {
			return null;
		}
		return matrix[channel.matrixCol][channel.matrixRow+1];
	}

        @Override
	public IChannelFunction[] getLeftNeighbours(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		IChannelFunction[] neighbours = new IChannelFunction[channel.matrixCol];
		for (int i=channel.matrixCol-1; i>=0; i--) {
			neighbours[i] = matrix[i][channel.matrixRow];
		}
		return neighbours;
	}

        @Override
	public IChannelFunction[] getRightNeighbours(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		IChannelFunction[] neighbours = new IChannelFunction[MATRIX_WIDTH-(channel.matrixCol+1)];
		for (int i=channel.matrixCol+1; i<MATRIX_WIDTH; i++) {
			neighbours[i] = matrix[i][channel.matrixRow];
		}
		return neighbours;
	}

        @Override
	public IChannelFunction[] getTopNeighbours(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		IChannelFunction[] neighbours = new IChannelFunction[channel.matrixRow];
		for (int i=channel.matrixRow-1; i>=0; i--) {
			neighbours[i] = matrix[channel.matrixCol][i];
		}
		return neighbours;
	}

        @Override
	public IChannelFunction[] getBottomNeighbours(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		IChannelFunction[] neighbours = new IChannelFunction[MATRIX_HEIGHT-(channel.matrixRow+1)];
		for (int i=channel.matrixRow+1; i<MATRIX_HEIGHT; i++) {
			neighbours[i] = matrix[i][channel.matrixRow];
		}
		return neighbours;
	}

        @Override
	public IChannelFunction[] getNearestNeighbours(IChannelFunction chn) {
		if (!(chn instanceof ChannelFunction)) {
			return null;
		}
		ChannelFunction channel = (ChannelFunction) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		IChannelFunction[] neighbours = new IChannelFunction[4];
		if (channel.matrixRow > 0) {
			neighbours[0] = matrix[channel.matrixCol][channel.matrixRow-1];
		}
		if (channel.matrixCol > 0) {
			neighbours[1] = matrix[channel.matrixCol-1][channel.matrixRow];
		}
		if (channel.matrixRow < MATRIX_HEIGHT-1) {
			neighbours[2] = matrix[channel.matrixCol][channel.matrixRow+1];
		}
		if (channel.matrixCol < MATRIX_WIDTH-1) {
			neighbours[3] = matrix[channel.matrixCol+1][channel.matrixRow];
		}

		return neighbours;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "eegChannel." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

	@Override
	public boolean isMutable() {
		return this.mutable;
	}

}
