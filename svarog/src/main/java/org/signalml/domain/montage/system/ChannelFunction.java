/* EegChannel.java created 2007-10-20
 *
 */
package org.signalml.domain.montage.system;

import org.signalml.domain.montage.system.ChannelType;

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

	UNKNOWN("Unknown", false),
	EEG("EEG", false),
	ECG("ECG", false),
	EMG("EMG", false),
	RESP("RESP", false),
	SAO2("SaO2", false),
	ZERO("ZERO", true),
	ONE("ONE", true);
	/**
	 * a name of this channel
	 */
	private String name;
	/**
	 * a variable telling if this channel is unique
	 */
	private boolean unique;
	/**
	 * a variable telling if this channel is mutable
	 */
	private boolean mutable;

	/**
	 * Constructor. Creates a channel of a given {@link ChannelType type}
	 * and puts it at given location.
	 * @param name the name of the channel
	 * @param unique is this channel unique?
	 */
	private ChannelFunction(String name, boolean unique) {
		this.mutable = true;
		this.name = name;
		this.unique = unique;
	}

	/**
	 * Constructor.
	 * @param name the name of the channel
	 * @param type the type of the channel
	 * @param unique is the channel unique?
	 * @param mutable is the channel mutable?
	 */
	private ChannelFunction(String name, boolean unique, boolean mutable) {
		this(name, unique);
		this.mutable = mutable;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isUnique() {
		return unique;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[]{"eegChannel." + this.toString()};
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
