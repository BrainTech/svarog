/* Channel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/**
 * Interface to describe types (functions) of channels and their location.
 * Allows to find neighbours for a given channel.
 * Its implementations are enumerators containing possible functions of channels
 * (e.g. for {@link GenericChannel GenericChannel} it will be
 * SIGNAL, REFERENCE, OTHER and UNKNOWN).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Channel extends MessageSourceResolvable, Serializable {

        /**
         *
         * @return the name of the channel
         */
	String getName();

        /**
         *
         * @return the type of the channel
         */
	ChannelType getType();

        /**
         *
         * @return true if the channel is unique, false otherwise
         */
	boolean isUnique();

        /**
         * Returns the number of a column in the matrix in which
         * the channel is located.
         * @return the number of a column in the matrix in which
         * the channel is located.
         */
	int getMatrixCol();

        /**
         * Returns the number of a row in the matrix in which
         * the channel is located.
         * @return the number of a row in the matrix in which
         * the channel is located.
         */
	int getMatrixRow();

        /**
         * Finds the left neighbour of a given channel
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the left neighbour of a given channel.
         * null if doesn't exist or given channel is not in the matrix
         */
	Channel getLeftNeighbour(Channel channel);

        /**
         * Finds the right neighbour of a given channel
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the right neighbour of a given channel.
         * null if doesn't exist or given channel is not in the matrix
         */
	Channel getRightNeighbour(Channel channel);

        /**
         * Finds the top neighbour of a given channel
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the top neighbour of a given channel.
         * null if doesn't exist or given channel is not in the matrix
         */
	Channel getTopNeighbour(Channel channel);

        /**
         * Finds the bottom neighbour of a given channel
         * @param channel the channel for which we are looking for a neighbour
         * @return the object which is the bottom neighbour of a given channel.
         * null if doesn't exist or given channel is not in the matrix
         */
	Channel getBottomNeighbour(Channel channel);

        /**
         * Finds all channels that are: a) in the matrix;  b) on the left of given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist, null if given channel is not in the matrix
         */
	Channel[] getLeftNeighbours(Channel channel);

        /**
         * Finds all channels that are: a) in the matrix;  b) on the right of given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist, null if given channel is not in the matrix
         */
	Channel[] getRightNeighbours(Channel channel);

        /**
         * Finds all channels that are: a) in the matrix;  b) above given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist, null if given channel is not in the matrix
         */
	Channel[] getTopNeighbours(Channel channel);

        /**
         * Finds all channels that are: a) in the matrix;  b) below given;
         * c) in the same row as given
         * @param channel the channel for which we are looking for neighbours
         * @return the array of found channels. An empty array if such channels
         * don't exist, null if given channel is not in the matrix
         */
	Channel[] getBottomNeighbours(Channel channel);

        /**
         * Returns an array which consists of top, left, bottom and right
         * neighbour (if they exist)
         * @param channel the channel for which we are looking for neighbours
         * @return Created array. null if given channel is not in the matrix
         */
	Channel[] getNearestNeighbours(Channel channel);

}
