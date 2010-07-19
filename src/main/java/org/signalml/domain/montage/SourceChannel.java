/* MontageChannel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a source channel. It is composed of a number, a name
 * and a {@link Channel function} of a channel.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sourcechannel")
public class SourceChannel implements Serializable {

	private static final long serialVersionUID = 1L;

        /**
         * index of a SourceChannel  >=0
         */
	private int channel;

        /**
         * String representing a label of a SourceChannel
         */
	private String label;

        /**
         * The function of a SourceChannel
         */
	private Channel function;

        /**
         * Constructor. Creates empty SourceChannel
         */
	protected SourceChannel() {
	}

        /**
         * Constructor. Creates a SourceChannel with a given index,
         * label and function
         * @param channel an index of a SourceChannel
         * @param label a label of a SourceChannel
         * @param function a function of a SourceChannel
         */
	public SourceChannel(int channel, String label, Channel function) {
		this.channel = channel;
		setLabel(label);
		setFunction(function);
	}

        /**
         * Copy constructor
         * @param sourceChannel a SourceChannel to be copied
         */
	public SourceChannel(SourceChannel sourceChannel) {
		this.channel = sourceChannel.channel;
		this.label = sourceChannel.label;
		this.function = sourceChannel.function;
	}

        /**
         *
         * @return an index of a SourceChannel
         */
	public int getChannel() {
		return channel;
	}

        /**
         *
         * @return a label of a SourceChannel
         */
	public String getLabel() {
		return label;
	}

        /**
         *
         * @return a function of a SourceChannel
         */
	public Channel getFunction() {
		return function;
	}

        /**
         *
         * @param label a String with a label to be set
         */
	public void setLabel(String label) {
		if (label == null) {
			throw new NullPointerException("Null label");
		}
		if (label.isEmpty()) {
			throw new IllegalArgumentException("Empty label");
		}
		this.label = label;
	}

        /**
         *
         * @param function a function to be set
         */
	public void setFunction(Channel function) {
		if (function == null) {
			throw new NullPointerException("Null function");
		}
		this.function = function;
	}

}
