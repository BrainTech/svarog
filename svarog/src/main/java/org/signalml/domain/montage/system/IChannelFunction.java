/* Channel.java created 2007-10-23
 *
 */
package org.signalml.domain.montage.system;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/**
 * This interface describes the types (functions) of channels and their location
 * (location of electrodes).
 * Allows to find neighbours for a given channel.
 * Its implementations are enumerators containing possible functions of channels
 * (e.g. for {@link GenericChannel GenericChannel} it will be
 * SIGNAL, REFERENCE, OTHER and UNKNOWN).
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

	int getMinValueScale();
	int getMaxValueScale();
	String getUnitOfMeasurementSymbol();
}
