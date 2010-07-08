/* Channel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/** Channel
 * Class representing types of channels and their location
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Channel extends MessageSourceResolvable, Serializable {

        /**
         *
         * @return name of a channel
         */
	String getName();

        /**
         *
         * @return type of a channel
         */
	ChannelType getType();

        /**
         *
         * @return true if channel is unique, false otherwise
         */
	boolean isUnique();

        /**
         * Returns number of columns in channels matrix
         * @return number of columns in channels matrix
         */
	int getMatrixCol();

        /**
         * Returns number of rows in channels matrix
         * @return number of rows in channels matrix
         */
	int getMatrixRow();

        /**
         * Finds left neighbour of a given channel
         * @param channel channel for which we are looking for a neighbour
         * @return Channel object which is a left neighbour of a given channel. null if doesn't exist or given channel is not in the matrix
         */
	Channel getLeftNeighbour(Channel channel);

        /**
         * Finds right neighbour of a given channel
         * @param channel channel for which we are looking for a neighbour
         * @return Channel object which is a right neighbour of a given channel. null if doesn't exist or given channel is not in the matrix
         */
	Channel getRightNeighbour(Channel channel);

        /**
         * Finds top neighbour of a given channel
         * @param channel channel for which we are looking for a neighbour
         * @return Channel object which is a top neighbour of a given channel. null if doesn't exist or given channel is not in the matrix
         */
	Channel getTopNeighbour(Channel channel);

        /**
         * Finds bottom neighbour of a given channel
         * @param channel channel for which we are looking for a neighbour
         * @return Channel object which is a bottom neighbour of a given channel. null if doesn't exist or given channel is not in the matrix
         */
	Channel getBottomNeighbour(Channel channel);

        /**
         * Finds all channels that are: a) in a matrix;  a) on the left of given; c) in the same row as given
         * @param channel channel for which we are looking for neighbours
         * @return array of found channels. Empty array if such channels doesn't exist, null if given channel is not in the matrix
         */
	Channel[] getLeftNeighbours(Channel channel);

        /**
         * Finds all channels that are: a) in a matrix;  a) on the right of given; c) in the same row as given
         * @param channel channel for which we are looking for neighbours
         * @return array of found channels. Empty array if such channels doesn't exist, null if given channel is not in the matrix
         */
	Channel[] getRightNeighbours(Channel channel);

        /**
         * Finds all channels that are: a) in a matrix;  a) above given; c) in the same column as given
         * @param channel channel for which we are looking for neighbours
         * @return array of found channels. Empty array if such channels doesn't exist, null if given channel is not in the matrix
         */
	Channel[] getTopNeighbours(Channel channel);

        /**
         * Finds all channels that are: a) in a matrix;  a) below given; c) in the same column as given
         * @param channel channel for which we are looking for neighbours
         * @return array of found channels. Empty array if such channels doesn't exist, null if given channel is not in the matrix
         */
	Channel[] getBottomNeighbours(Channel channel);

        /**
         * Returns neighbours in an array of top, left, bottom, right (if exist)
         * @param channel channel for which we are looking for neighbours
         * @return Created array. null if given channel is not in the matrix
         */
	Channel[] getNearestNeighbours(Channel channel);

}
