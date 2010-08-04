/* Channel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/**
 * This interface describes the types (functions) of channels and their location.
 * Allows to find neighbours for a given channel.
 * Its implementations are enumerators containing possible functions of channels
 * (e.g. for {@link GenericChannel GenericChannel} it will be
 * SIGNAL, REFERENCE, OTHER and UNKNOWN).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Channel extends MessageSourceResolvable, Serializable {

        /**
         * Returns the name of this channel.
         * @return the name of this channel
         */
	String getName();

        /**
         * Returns the type of this channel.
         * @return the type of this channel
         */
	ChannelType getType();

        /**
         * Returns if this channel is unique.
         * @return true if this channel is unique, false otherwise
         */
	boolean isUnique();

        /**
         * Returns the number of a column in the matrix in which
         * this channel is located.
         * @return the number of a column in the matrix in which
         * this channel is located.
         */
	int getMatrixCol();

        /**
         * Returns the number of a row in the matrix in which
         * this channel is located.
         * @return the number of a row in the matrix in which
         * this channel is located.
         */
	int getMatrixRow();

        /**
         * Finds the left neighbour of a given channel.
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the left neighbour of a given channel
         * or null if doesn't exist or given channel is not in the matrix
         */
	Channel getLeftNeighbour(Channel channel);

        /**
         * Finds the right neighbour of a given channel.
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the right neighbour of a given channel
         * or null if doesn't exist or given channel is not in the matrix
         */
	Channel getRightNeighbour(Channel channel);

        /**
         * Finds the top neighbour of a given channel.
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the top neighbour of a given channel
         * or null if doesn't exist or given channel is not in the matrix
         */
	Channel getTopNeighbour(Channel channel);

        /**
         * Finds the bottom neighbour of a given channel
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the bottom neighbour of a given channel
         * or null if doesn't exist or given channel is not in the matrix
         */
	Channel getBottomNeighbour(Channel channel);

        /**
         * Finds all channels that satisfies all following conditions:
         * a) in the matrix;  b) on the left of given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist or null if given channel is not in the matrix
         */
	Channel[] getLeftNeighbours(Channel channel);

        /**
         * Finds all channels that satisfies all following conditions:
         * a) in the matrix;  b) on the right of given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist or null if given channel is not in the matrix
         */
	Channel[] getRightNeighbours(Channel channel);

        /**
         * Finds all channels that satisfies all following conditions:
         * a) in the matrix;  b) above given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist or null if given channel is not in the matrix
         */
	Channel[] getTopNeighbours(Channel channel);

        /**
         * Finds all channels that satisfies all following conditions:
         * a) in the matrix;  b) below given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist or null if given channel is not in the matrix
         */
	Channel[] getBottomNeighbours(Channel channel);

        /**
         * Returns an array which consists of top, left, bottom and right
         * neighbour (if they exist).
         * @param channel the channel for which we are looking for neighbours
         * @return Created array or null if given channel is not in the matrix
         */
	Channel[] getNearestNeighbours(Channel channel);

}
