/* GenericChannel.java created 2007-11-29
 *
 */

package org.signalml.domain.montage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** GenericChannel
 * Class representing a generic channel
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("genericchannel")
public enum GenericChannel implements Channel {

	/**
         * default type of channel
         */
	UNKNOWN("Unknown", ChannelType.UNKNOWN, false, null),

	// Generics
	SIGNAL("Signal", ChannelType.PRIMARY, false, null),
	REFERENCE("Reference", ChannelType.REFERENCE, false, null),
	OTHER("Other", ChannelType.OTHER, false, null),

	;

        /**
         * name of a channel
         */
	private String name;

        /**
         * type of a channel. Possible types:
         * UNKNOWN, PRIMARY, REFERENCE, OTHER
         */
	private ChannelType type;

        /**
         *
         */
	private Pattern matchingPattern;

        /**
         * variable telling if channel is unique
         */
	private boolean unique;


        /**
         * Constructor
         * @param name name of the channel
         * @param type type of the channel
         * @param unique is the channel unique?
         * @param pattern regular expression which will be used to search channel by name
         */
	private GenericChannel(String name, ChannelType type, boolean unique, String pattern) {
		this.name = name;
		this.type = type;
		this.unique = unique;
		if (pattern != null) {
			this.matchingPattern = Pattern.compile(pattern);
		}
	}

        /**
         * Returns a GenericChannel of a given name
         * @param name name of channel to be found
         * @return a GenericChannel of a given name
         */
	public static GenericChannel forName(String name) {
		GenericChannel[] values = values();
		Matcher matcher;
		for (GenericChannel channel : values) {
			if (channel.matchingPattern != null) {
				matcher = channel.matchingPattern.matcher(name);
				if (matcher.matches()) {
					return channel;
				}
			}
		}
		return null;
	}

        /**
         *
         * @return name of a channel
         */
	public String getName() {
		return name;
	}

        /**
         *
         * @return type of a channel
         */
	public ChannelType getType() {
		return type;
	}

        /**
         *
         * @return true if channel is unique, false otherwise
         */
	@Override
	public boolean isUnique() {
		return unique;
	}

        /**
         *
         * @return pattern which is used to search channel by name
         */
	public Pattern getMatchingPattern() {
		return matchingPattern;
	}

         /**
         * Returns number of columns in channels matrix. Always -1
         * @return number of columns in channels matrix.
         */
	@Override
	public int getMatrixCol() {
		return -1;
	}

        /**
         * Returns number of rows in channels matrix. Always -1
         * @return number of rows in channels matrix.
         */
	@Override
	public int getMatrixRow() {
		return -1;
	}

        /**
         * Finds left neighbour of a given channel
         * @param chn channel for which we are looking for a neighbour
         * @return channel is not in the matrix so null
         */
	public Channel getLeftNeighbour(Channel chn) {
		return null;
	}

        /**
         * Finds right neighbour of a given channel
         * @param chn channel for which we are looking for a neighbour
         * @return channel is not in the matrix so null
         */
	public Channel getRightNeighbour(Channel chn) {
		return null;
	}

        /**
         * Finds top neighbour of a given channel
         * @param chn channel for which we are looking for a neighbour
         * @return channel is not in the matrix so null
         */
	public Channel getTopNeighbour(Channel chn) {
		return null;
	}

        /**
         * Finds bottom neighbour of a given channel
         * @param chn channel for which we are looking for a neighbour
         * @return channel is not in the matrix so null
         */
	public Channel getBottomNeighbour(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in a matrix;  a) on the left of given; c) in the same row as given
         * @param chn channel for which we are looking for neighbours
         * @return channel is not in the matrix so null
         */
	public Channel[] getLeftNeighbours(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in a matrix;  a) on the right of given; c) in the same row as given
         * @param chn channel for which we are looking for neighbours
         * @return channel is not in the matrix so null
         */
	public Channel[] getRightNeighbours(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in a matrix;  a) above given; c) in the same row as given
         * @param chn channel for which we are looking for neighbours
         * @return channel is not in the matrix so null
         */
	public Channel[] getTopNeighbours(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in a matrix;  a) below given; c) in the same row as given
         * @param chn channel for which we are looking for neighbours
         * @return channel is not in the matrix so null
         */
	public Channel[] getBottomNeighbours(Channel chn) {
		return null;
	}

	/**
         * Returns neighbours in an array of top, left, bottom, right (if exist)
         * @param chn channel for which we are looking for neighbours
         * @return given channel is not in the matrix so null
         */
	public Channel[] getNearestNeighbours(Channel chn) {
		return null;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "genericChannel." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}
