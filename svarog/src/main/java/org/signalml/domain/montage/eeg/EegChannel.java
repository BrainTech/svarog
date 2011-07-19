/* EegChannel.java created 2007-10-20
 *
 */

package org.signalml.domain.montage.eeg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.signalml.domain.montage.Channel;
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
public enum EegChannel implements Channel {

	// XXX the decision to make this an enum was probably wrong
	// this should be a normal class with static final fields, unfortunately no time for refactoring now

	/**
         * the default EEG channel
         */
	UNKNOWN("Unknown", ChannelType.UNKNOWN, false, null),

	// Generics
	EEG("EEG", ChannelType.PRIMARY, false, null),
	REFERENCE("Reference", ChannelType.REFERENCE, false, null),
	OTHER("Other", ChannelType.OTHER, false, null),

	// nose - zeroe'th row - no known electrode name?
	NOSE("Nose", ChannelType.REFERENCE, true, null, 3, 0),

	// EEG - first row
	FP1("Fp1", ChannelType.PRIMARY, true, "[Ff][Pp]\\s*1", 2, 1),
	FPZ("Fpz", ChannelType.PRIMARY, true, "[Ff][Pp][Zz]", 3, 1),
	FP2("Fp2", ChannelType.PRIMARY, true, "[Ff][Pp]\\s*2", 4, 1),
	// EEG - second row
	F7("F7", ChannelType.PRIMARY, true, "[Ff]\\s*7", 1, 2),
	F3("F3", ChannelType.PRIMARY, true, "[Ff]\\s*3", 2, 2),
	FZ("Fz", ChannelType.PRIMARY, true, "[Ff][Zz]", 3, 2),
	F4("F4", ChannelType.PRIMARY, true, "[Ff]\\s*4", 4, 2),
	F8("F8", ChannelType.PRIMARY, true, "[Ff]\\s*8", 5, 2),
	// EARS & EEG - third row
	A1("A1", ChannelType.REFERENCE, true, "[Aa]\\s*1", 0, 3),
	T3("T3", ChannelType.PRIMARY, true, "[Tt]\\s*3", 1, 3),
	C3("C3", ChannelType.PRIMARY, true, "[Cc]\\s*3", 2, 3),
	CZ("Cz", ChannelType.PRIMARY, true, "[Cc][Zz]", 3, 3),
	C4("C4", ChannelType.PRIMARY, true, "[Cc]\\s*4", 4, 3),
	T4("T4", ChannelType.PRIMARY, true, "[Tt]\\s*4", 5, 3),
	A2("A2", ChannelType.REFERENCE, true, "[Aa]\\s*2", 6, 3),
	// EEG - fourth row
	T5("T5", ChannelType.PRIMARY, true, "[Tt]\\s*5", 1, 4),
	P3("P3", ChannelType.PRIMARY, true, "[Pp]\\s*3", 2, 4),
	PZ("Pz", ChannelType.PRIMARY, true, "[Pp][Zz]", 3, 4),
	P4("P4", ChannelType.PRIMARY, true, "[Pp]\\s*4", 4, 4),
	T6("T6", ChannelType.PRIMARY, true, "[Tt]\\s*6", 5, 4),
	// EEG - fifth row
	O1("O1", ChannelType.PRIMARY, true, "[Oo]\\s*1", 2, 5),
	OZ("OZ", ChannelType.PRIMARY, true, "[Oo][Zz]", 3, 5),
	O2("O2", ChannelType.PRIMARY, true, "[Oo]\\s*2", 4, 5),
	// EOG
	EOGL("EOGL", ChannelType.OTHER, true, "[Ee][Oo][Gg]\\s*[Ll]"),
	EOGP("EOGP", ChannelType.OTHER, true, "[Ee][Oo][Gg]\\s*[Pp]"),
	// The rest
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
	private static EegChannel[][] matrix;

        /**
         * Creates channels matrix.
         */
	static {

		matrix = new EegChannel[MATRIX_WIDTH][MATRIX_HEIGHT];
		EegChannel[] all = values();
		for (EegChannel c : all) {
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
	private EegChannel(String name, ChannelType type, boolean unique, String pattern, int matrixCol, int matrixRow) {
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
	private EegChannel(String name, ChannelType type, boolean unique, String pattern) {
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
	private EegChannel(String name, ChannelType type, boolean unique, boolean mutable, String pattern) {
		this(name, type, unique, pattern);
		this.mutable=mutable;
	}

        /**
         * Finds an EegChannel of a given name.
         * @param name the name of a channel to be found
         * @return an EegChannel of a given name
         */
	public static EegChannel forName(String name) {
		EegChannel[] values = values();
		Matcher matcher;
		for (EegChannel channel : values) {
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
	public static Channel[][] getMatrix() {
		return matrix;
	}

        @Override
	public Channel getLeftNeighbour(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixCol <= 0) {
			return null;
		}
		return matrix[channel.matrixCol-1][channel.matrixRow];
	}

        @Override
	public Channel getRightNeighbour(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixCol >= MATRIX_WIDTH-1) {
			return null;
		}
		return matrix[channel.matrixCol+1][channel.matrixRow];
	}

        @Override
	public Channel getTopNeighbour(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixRow <= 0) {
			return null;
		}
		return matrix[channel.matrixCol][channel.matrixRow-1];
	}

        @Override
	public Channel getBottomNeighbour(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		if (channel.matrixRow >= MATRIX_HEIGHT-1) {
			return null;
		}
		return matrix[channel.matrixCol][channel.matrixRow+1];
	}

        @Override
	public Channel[] getLeftNeighbours(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		Channel[] neighbours = new Channel[channel.matrixCol];
		for (int i=channel.matrixCol-1; i>=0; i--) {
			neighbours[i] = matrix[i][channel.matrixRow];
		}
		return neighbours;
	}

        @Override
	public Channel[] getRightNeighbours(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		Channel[] neighbours = new Channel[MATRIX_WIDTH-(channel.matrixCol+1)];
		for (int i=channel.matrixCol+1; i<MATRIX_WIDTH; i++) {
			neighbours[i] = matrix[i][channel.matrixRow];
		}
		return neighbours;
	}

        @Override
	public Channel[] getTopNeighbours(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		Channel[] neighbours = new Channel[channel.matrixRow];
		for (int i=channel.matrixRow-1; i>=0; i--) {
			neighbours[i] = matrix[channel.matrixCol][i];
		}
		return neighbours;
	}

        @Override
	public Channel[] getBottomNeighbours(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		Channel[] neighbours = new Channel[MATRIX_HEIGHT-(channel.matrixRow+1)];
		for (int i=channel.matrixRow+1; i<MATRIX_HEIGHT; i++) {
			neighbours[i] = matrix[i][channel.matrixRow];
		}
		return neighbours;
	}

        @Override
	public Channel[] getNearestNeighbours(Channel chn) {
		if (!(chn instanceof EegChannel)) {
			return null;
		}
		EegChannel channel = (EegChannel) chn;
		if (channel.matrixCol < 0 || channel.matrixRow < 0) {
			return null;
		}
		Channel[] neighbours = new Channel[4];
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
