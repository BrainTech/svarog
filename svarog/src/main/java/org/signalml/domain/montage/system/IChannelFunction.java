package org.signalml.domain.montage.system;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/**
 * This interface represents a function of a channel - that is what kind of signal
 * it 'transfers', what is the unit of measurement of the signal and its
 * minimum and maximum values.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface IChannelFunction extends MessageSourceResolvable, Serializable {

	/**
	 * Returns the name of this channel.
	 * @return the name of this channel
	 */
	String getName();

	/**
	 * Returns if this channel is unique.
	 * @return true if this channel is unique, false otherwise
	 */
	boolean isUnique();

	/**
	 * Returns if this channel is mutable
	 * @return true if this channel is mutable, false otherwise
	 */
	boolean isMutable();

	/**
	 * Returns the minimum value that should be set on the value scale for the signal.
	 * @return
	 */
	int getMinValue();

	/**
	 * Returns the maximum value that should be set on the value scale for the signal.
	 * @return
	 */
	int getMaxValue();

	/**
	 * Returns the String representing a unit of measurement for the channel
	 * (e.g. 'uV' or 'mV').
	 * @return
	 */
	String getUnitOfMeasurementSymbol();
}
