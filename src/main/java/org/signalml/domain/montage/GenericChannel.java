/* GenericChannel.java created 2007-11-29
 *
 */

package org.signalml.domain.montage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class representing a generic channel.
 * There is no matrix for generic channels so they don't have any neighbours.
 * @see Channel
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("genericchannel")
public enum GenericChannel implements Channel {

	/**
         * default type of the channel
         */
	UNKNOWN("Unknown", ChannelType.UNKNOWN, false, null),

	// Generics
	SIGNAL("Signal", ChannelType.PRIMARY, false, null),
	REFERENCE("Reference", ChannelType.REFERENCE, false, null),
	OTHER("Other", ChannelType.OTHER, false, null),

	;

        /**
         * name of the channel
         */
	private String name;

        /**
         * type of the channel. Possible types:
         * UNKNOWN, PRIMARY, REFERENCE, OTHER
         */
	private ChannelType type;

        /**
         * Pattern which will be used to search channel by name.
         */
	private Pattern matchingPattern;

        /**
         * a variable telling if the channel is unique
         */
	private boolean unique;


        /**
         * Constructor
         * @param name the name of the channel
         * @param type the type of the channel
         * @param unique is the channel unique?
         * @param pattern the regular expression which will be used to search channel by name
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
         * Finds a GenericChannel of a given name
         * @param name the name of a channel to be found
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
         * Returns the name of the channel
         * @return the name of the channel
         */
	public String getName() {
		return name;
	}

        /**
         * Returns the type of the channel
         * @return the type of the channel
         */
	public ChannelType getType() {
		return type;
	}

        /**
         * Returns if the channel is unique
         * @return true if the channel is unique, false otherwise
         */
	@Override
	public boolean isUnique() {
		return unique;
	}

        /**
         * Returns the pattern which is used to search channel by name
         * @return the pattern which is used to search channel by name
         */
	public Pattern getMatchingPattern() {
		return matchingPattern;
	}

         /**
         * Returns the number of a column in the matrix in which
         * the channel is located. Always -1.
         * @return the number of a column in the matrix in which
         * the channel is located.
         */
	@Override
	public int getMatrixCol() {
		return -1;
	}

        /**
         * Returns the number of a row in the matrix in which
         * the channel is located. Always -1.
         * @return the number of a row in the matrix in which
         * the channel is located.
         */
	@Override
	public int getMatrixRow() {
		return -1;
	}

        /**
         * Finds the left neighbour of a given channel
         * @param chn the channel for which we are looking for a neighbour
         * @return the channel is not in the matrix so null
         */
	public Channel getLeftNeighbour(Channel chn) {
		return null;
	}

        /**
         * Finds the right neighbour of a given channel
         * @param chn the channel for which we are looking for a neighbour
         * @return the channel is not in the matrix so null
         */
	public Channel getRightNeighbour(Channel chn) {
		return null;
	}

        /**
         * Finds the top neighbour of a given channel
         * @param chn the channel for which we are looking for a neighbour
         * @return the channel is not in the matrix so null
         */
	public Channel getTopNeighbour(Channel chn) {
		return null;
	}

        /**
         * Finds the bottom neighbour of a given channel
         * @param chn the channel for which we are looking for a neighbour
         * @return the channel is not in the matrix so null
         */
	public Channel getBottomNeighbour(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in the matrix;  a) on the left of given; c) in the same row as given
         * @param chn the channel for which we are looking for neighbours
         * @return the channel is not in the matrix so null
         */
	public Channel[] getLeftNeighbours(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in the matrix;  b) on the right of given; c) in the same row as given
         * @param chn the channel for which we are looking for neighbours
         * @return the channel is not in the matrix so null
         */
	public Channel[] getRightNeighbours(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in the matrix;  b) above given; c) in the same row as given
         * @param chn the channel for which we are looking for neighbours
         * @return the channel is not in the matrix so null
         */
	public Channel[] getTopNeighbours(Channel chn) {
		return null;
	}

	/**
         * Finds all channels that are: a) in the matrix;  b) below given; c) in the same row as given
         * @param chn the channel for which we are looking for neighbours
         * @return the channel is not in the matrix so null
         */
	public Channel[] getBottomNeighbours(Channel chn) {
		return null;
	}

	/**
         * Returns an array which consists of top, left, bottom and right neighbour (if they exist)
         * @param chn the channel for which we are looking for neighbours
         * @return the channel is not in the matrix so null
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
