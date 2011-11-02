/* MontageChannel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import org.signalml.domain.montage.system.IChannelFunction;
import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.system.EegElectrode;

/**
 * This class represents a source channel. It has a certain number, name
 * and {@link IChannelFunction function}.
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
	private IChannelFunction function;

	/**
	 * An {@link EegElectrode} associated with this {@link SourceChannel}.
	 * If the channel function is different than EEG, then this field
	 * will be null.
	 */
	private EegElectrode eegElectrode;

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
	public SourceChannel(int channel, String label, IChannelFunction function) {
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
		this.eegElectrode = sourceChannel.eegElectrode;
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
	public IChannelFunction getFunction() {
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
	public void setFunction(IChannelFunction function) {
		if (function == null) {
			throw new NullPointerException("Null function");
		}
		this.function = function;
	}

	/**
	 * Sets an {@link EegElectrode} that will be associated with this
	 * {@link SourceChannel}.
	 * @param eegElectrode an {@link EegElectrode} that will be associated with this
	 * {@link SourceChannel}
	 */
	public void setEegElectrode(EegElectrode eegElectrode) {
		this.eegElectrode = eegElectrode;
	}

	/**
	 * Returns the {@link EegElectrode} that is associated with this
	 * {@link SourceChannel}.
	 * @return the {@link EegElectrode} that is associated with this
	 * {@link SourceChannel}
	 */
	public EegElectrode getEegElectrode() {
		return eegElectrode;
	}

	/**
	 * Returns if this channel is of a given type, taking into account
	 * that this channel's type may be null.
	 * @param type the type to be checked
	 * @return true if this channel's type is equal to the given type
	 */
	public boolean isChannelType(ChannelType type) {
		if (eegElectrode != null &&
			eegElectrode.getChannelType() == type)
			return true;
		else
			return false;
	}

}
