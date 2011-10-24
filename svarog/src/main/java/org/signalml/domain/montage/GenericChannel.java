/* GenericChannel.java created 2007-11-29
 *
 */

package org.signalml.domain.montage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a generic channel.
 * There is no matrix for generic channels so they don't have any neighbours.
 * @see Channel
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("genericchannel")
public enum GenericChannel implements IChannelFunction {

	/**
         * the default type of the channel
         */
	UNKNOWN("Unknown", ChannelType.UNKNOWN, false, null),

	// Generics
	SIGNAL("Signal", ChannelType.PRIMARY, false, null),
	REFERENCE("Reference", ChannelType.REFERENCE, false, null),
	OTHER("Other", ChannelType.OTHER, false, null),

	;

        /**
         * the name of this channel
         */
	private String name;

        /**
         * type of this channel. Possible types:
         * UNKNOWN, PRIMARY, REFERENCE, OTHER
         */
	private ChannelType type;

        /**
         * the pattern which will be used to search this channel by name.
         */
	private Pattern matchingPattern;

        /**
         * a variable telling if this channel is unique
         */
	private boolean unique;
	
    /**
     * a variable telling if this channel is mutable
     */
	private boolean mutable=true;


        /**
         * Constructor.
         * @param name the name of the channel
         * @param type the type of the channel
         * @param unique is the channel unique?
         * @param pattern the regular expression which will be used to search
         * channel by name
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
     * Constructor.
     * @param name the name of the channel
     * @param type the type of the channel
     * @param unique is the channel unique?
     * @param unique is the channel mutable?
     * @param pattern the regular expression which will be used to search
     * channel by name
     */
	private GenericChannel(String name, ChannelType type, boolean unique, boolean mutable, String pattern) {
		this(name, type, unique, pattern);
		this.mutable=mutable;
	}

        /**
         * Finds a GenericChannel of a given name.
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
         * Returns the pattern which is used to search this channel by name
         * @return the pattern which is used to search this channel by name
         */
	public Pattern getMatchingPattern() {
		return matchingPattern;
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
	@Override
	public boolean isMutable() {
		return this.mutable;
	}

}
