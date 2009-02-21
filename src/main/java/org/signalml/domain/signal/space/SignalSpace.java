/* SignalSpace.java created 2008-01-18
 * 
 */

package org.signalml.domain.signal.space;

import java.io.Serializable;

import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.tag.Tag;

/** SignalSpace
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSpace implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private SignalSourceLevel signalSourceLevel;
	
	private TimeSpaceType timeSpaceType;
	private ChannelSpaceType channelSpaceType;
	
	private boolean wholeSignalCompletePagesOnly;
	private SignalSelection selectionTimeSpace;
	private MarkerTimeSpace markerTimeSpace;
	
	private ChannelSpace channelSpace;
	
	public SignalSpace() {
		signalSourceLevel = SignalSourceLevel.FILTERED;
		timeSpaceType = TimeSpaceType.WHOLE_SIGNAL;
		channelSpaceType = ChannelSpaceType.WHOLE_SIGNAL;
		wholeSignalCompletePagesOnly = false;
	}

	public SignalSourceLevel getSignalSourceLevel() {
		return signalSourceLevel;
	}

	public void setSignalSourceLevel(SignalSourceLevel signalSourceLevel) {
		this.signalSourceLevel = signalSourceLevel;
	}

	public TimeSpaceType getTimeSpaceType() {
		return timeSpaceType;
	}

	public void setTimeSpaceType(TimeSpaceType timeSpaceType) {
		this.timeSpaceType = timeSpaceType;
	}

	public ChannelSpaceType getChannelSpaceType() {
		return channelSpaceType;
	}

	public void setChannelSpaceType(ChannelSpaceType channelSpaceType) {
		this.channelSpaceType = channelSpaceType;
	}

	public SignalSelection getSelectionTimeSpace() {
		return selectionTimeSpace;
	}
	
	public void setSelectionTimeSpace(SignalSelection selectionTimeSpace) {
		this.selectionTimeSpace = selectionTimeSpace;
	}

	public MarkerTimeSpace getMarkerTimeSpace() {
		return markerTimeSpace;
	}
	
	public void setMarkerTimeSpace(MarkerTimeSpace markerTimeSpace) {
		this.markerTimeSpace = markerTimeSpace;
	}

	public ChannelSpace getChannelSpace() {
		return channelSpace;
	}
	
	public void setChannelSpace(ChannelSpace channelSpace) {
		this.channelSpace = channelSpace;
	}	
	
	public boolean isWholeSignalCompletePagesOnly() {
		return wholeSignalCompletePagesOnly;
	}

	public void setWholeSignalCompletePagesOnly(boolean wholeSignalCompletePagesOnly) {
		this.wholeSignalCompletePagesOnly = wholeSignalCompletePagesOnly;
	}

	public void configureFromSelections( SignalSelection signalSelection, Tag tagSelection ) {
		
		if( signalSelection != null ) {
			
			setTimeSpaceType( TimeSpaceType.SELECTION_BASED );
			setSelectionTimeSpace(signalSelection);
			
			if( signalSelection.getType().isChannel() ) {
				
				setChannelSpaceType( ChannelSpaceType.SELECTED );
				
				ChannelSpace channelSpace = new ChannelSpace();
				channelSpace.addChannel( signalSelection.getChannel() );
				
				setChannelSpace(channelSpace);
				
			} else {
				
				setChannelSpaceType( ChannelSpaceType.WHOLE_SIGNAL );
				setChannelSpace(null);
			}
			
		}
		else {
			
			if( tagSelection != null ) {
				if( tagSelection.isMarker() && tagSelection.getType().isChannel() ) {
						
					setTimeSpaceType( TimeSpaceType.MARKER_BASED );
					
					MarkerTimeSpace markerTimeSpace = new MarkerTimeSpace();
					markerTimeSpace.setMarkerChannel( tagSelection.getChannel() );
					markerTimeSpace.setMarkerStyleName( tagSelection.getStyle().getName() );
					
					markerTimeSpace.setSecondsAfter(1.0);
					markerTimeSpace.setSecondsBefore(0.0);
					
					setMarkerTimeSpace(markerTimeSpace);
					
					setChannelSpaceType( ChannelSpaceType.WHOLE_SIGNAL );
					setChannelSpace(null);
						
				}
				
			}
						
		}
		
		
	}
	
}
