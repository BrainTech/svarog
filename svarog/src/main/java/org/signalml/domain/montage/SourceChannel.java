/* MontageChannel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a source channel. It has a certain number, name
 * and {@link Channel function}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sourcechannel")
public class SourceChannel implements Serializable {

	private static final long serialVersionUID = 1L;

        /**
         * an index of this SourceChannel  >=0
         */
	private int channel;

        /**
         * String representing a label of this SourceChannel
         */
	private String label;

        /**
         * the function of this SourceChannel
         */
	private Channel function;

        /**
         * Constructor. Creates an empty SourceChannel.
         */
	protected SourceChannel() {
	}

        /**
         * Constructor. Creates a SourceChannel with a given index,
         * label and function.
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
         * Copy constructor.
         * @param sourceChannel a SourceChannel to be copied
         */
	public SourceChannel(SourceChannel sourceChannel) {
		this.channel = sourceChannel.channel;
		this.label = sourceChannel.label;
		this.function = sourceChannel.function;
	}

        /**
         * Returns an index of this SourceChannel.
         * @return an index of this SourceChannel
         */
	public int getChannel() {
		return channel;
	}

        /**
         * Returns a label of this SourceChannel.
         * @return a label of this SourceChannel
         */
	public String getLabel() {
		return label;
	}

        /**
         * Returns a function of this SourceChannel.
         * @return a function of this SourceChannel
         */
	public Channel getFunction() {
		return function;
	}

        /**
         * Sets a label of this SourceChannel.
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
         * Sets a function of this SourceChannel.
         * @param function a function to be set
         */
	public void setFunction(Channel function) {
		if (function == null) {
			throw new NullPointerException("Null function");
		}
		this.function = function;
	}

}
