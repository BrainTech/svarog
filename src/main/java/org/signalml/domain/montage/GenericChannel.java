/* GenericChannel.java created 2007-11-29
 *
 */

package org.signalml.domain.montage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** GenericChannel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("genericchannel")
public enum GenericChannel implements Channel {

	// ???
	UNKNOWN("Unknown", ChannelType.UNKNOWN, false, null),

	// Generics
	SIGNAL("Signal", ChannelType.PRIMARY, false, null),
	REFERENCE("Reference", ChannelType.REFERENCE, false, null),
	OTHER("Other", ChannelType.OTHER, false, null),

	;

	private String name;
	private ChannelType type;
	private Pattern matchingPattern;
	private boolean unique;

	private GenericChannel(String name, ChannelType type, boolean unique, String pattern) {
		this.name = name;
		this.type = type;
		this.unique = unique;
		if (pattern != null) {
			this.matchingPattern = Pattern.compile(pattern);
		}
	}

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

	public String getName() {
		return name;
	}

	public ChannelType getType() {
		return type;
	}

	@Override
	public boolean isUnique() {
		return unique;
	}

	public Pattern getMatchingPattern() {
		return matchingPattern;
	}

	@Override
	public int getMatrixCol() {
		return -1;
	}

	@Override
	public int getMatrixRow() {
		return -1;
	}

	public Channel getLeftNeighbour(Channel chn) {
		return null;
	}

	public Channel getRightNeighbour(Channel chn) {
		return null;
	}

	public Channel getTopNeighbour(Channel chn) {
		return null;
	}

	public Channel getBottomNeighbour(Channel chn) {
		return null;
	}

	/** Returns neighbours to the left, begining with the closest neighbour.
	 *
	 * @param channel
	 * @return neighbours or null if channel is not in the matrix
	 */
	public Channel[] getLeftNeighbours(Channel chn) {
		return null;
	}

	/** Returns neighbours to the right, begining with the closest neighbour.
	 *
	 * @param channel
	 * @return neighbours or null if channel is not in the matrix
	 */
	public Channel[] getRightNeighbours(Channel chn) {
		return null;
	}

	/** Returns neighbours above, begining with the closest neighbour.
	 *
	 * @param channel
	 * @return neighbours or null if channel is not in the matrix
	 */
	public Channel[] getTopNeighbours(Channel chn) {
		return null;
	}

	/** Returns neighbours above, begining with the closest neighbour.
	 *
	 * @param channel
	 * @return neighbours or null if channel is not in the matrix
	 */
	public Channel[] getBottomNeighbours(Channel chn) {
		return null;
	}

	/** Returns neighbours in a 4-element array of top, left, bottom, right.
	 *
	 * @param channel
	 * @return neighbours or null if channel is not in the matrix
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
