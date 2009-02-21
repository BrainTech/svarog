/* Channel.java created 2007-10-23
 * 
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/** Channel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Channel extends MessageSourceResolvable, Serializable {

	String getName();
	ChannelType getType();
	boolean isUnique();
	
	int getMatrixCol();
	int getMatrixRow();
		
	Channel getLeftNeighbour( Channel channel );
	Channel getRightNeighbour( Channel channel );
	Channel getTopNeighbour( Channel channel );
	Channel getBottomNeighbour( Channel channel );
	
	Channel[] getLeftNeighbours( Channel channel );
	Channel[] getRightNeighbours( Channel channel );
	Channel[] getTopNeighbours( Channel channel );
	Channel[] getBottomNeighbours( Channel channel );
	
	Channel[] getNearestNeighbours( Channel channel );
	
}
