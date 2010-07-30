/* SignalSelection.java created 2007-10-03
 *
 */

package org.signalml.domain.signal;

import java.io.Serializable;

/** SignalSelection
 *  This class represents a selected part of a signal.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelection implements Serializable {

        /**
         * constant saying that no chanel is selected
         */
	public static final int CHANNEL_NULL = -1;

        //TODO uzupełnić
	private static final long serialVersionUID = 1L;

        /**
         * type of signal selection
         */
	protected SignalSelectionType type;

        /**
         * position where selection starts >= 0
         */
	protected float position;

        /**
         * length of selection in seconds >= 0
         */
	protected float length;

        /**
         * number of selected channel
         * CHANNEL_NULL when no channel is selected
         */
	protected int channel;

        /**
         * constructor creating selection of a given type
         * @param type type of selection
         * @throws NullPointerException if no type given
         *
         */
	public SignalSelection(SignalSelectionType type) {
		if (type == null) {
			throw new NullPointerException("No type");
		}
		this.type = type;
		this.channel = CHANNEL_NULL;
	}

        /**
         * constructor
         * @param type type of selection
         * @param position position where selection starts
         * @param length length of selection in seconds
         * @throws NullPointerException if no type given
         */
	public SignalSelection(SignalSelectionType type, float position, float length) {
		if (type == null) {
			throw new NullPointerException("No type");
		}
		this.type = type;
		this.position = position;
		this.length = length;
		this.channel = CHANNEL_NULL;
	}

        /**
         * constructor
         * @param type type of selection
         * @param position position where selection starts
         * @param length length of selection in seconds
         * @param channel number of selected channel
         * CHANNEL_NULL when no channel is selected
         */
	public SignalSelection(SignalSelectionType type, float position, float length, int channel) {
		this.type = type;
		this.position = position;
		this.length = length;
		this.channel = ((type != SignalSelectionType.CHANNEL || channel < 0) ? CHANNEL_NULL : channel);
	}

        /**
         * copy constructor
         * @param selection selection to be copied
         */
	public SignalSelection(SignalSelection selection) {
		this.position = selection.position;
		this.length = selection.length;
		this.channel = selection.channel;
		this.type = selection.type;
	}

        /**
         * sets parameters of selection
         * @param position position where selection starts
         * @param length length of selection in seconds
         * @param channel number of selected channel
         * CHANNEL_NULL when no channel is selected
         */
	public void setParameters(float position, float length, int channel) {
		this.position = position;
		this.length = length;
		this.channel = ((type != SignalSelectionType.CHANNEL || channel < 0) ? CHANNEL_NULL : channel);
	}

        /**
         * sets parameters of selection
         * @param position position where selection starts
         * @param length length of selection in seconds
         */
	public void setParameters(float position, float length) {
		this.position = position;
		this.length = length;
	}

        /**
         *
         * @return type of selection
         */
	public SignalSelectionType getType() {
		return type;
	}

        /**
         *
         * @return position where selection starts
         */
	public float getPosition() {
		return position;
	}

        /**
         *
         * @param position position where selection starts
         */
	public void setPosition(float position) {
		this.position = position;
	}

        /**
         *
         * @return length of selection in seconds
         */
	public float getLength() {
		return length;
	}

        /**
         *
         * @param length length of selection in seconds
         */
	public void setLength(float length) {
		this.length = length;
	}

        /**
         * returns position of the middle of selection
         * @return middle of selection
         */
	public float getCenterPosition() {
		return position + length / 2;
	}

        /**
         * returns position where selection is ending
         * @return position where selection is ending
         */
	public float getEndPosition() {
		return position + length;
	}

        /**
         *
         * @return number of selected channel
         * CHANNEL_NULL when no channel is selected
         */
	public int getChannel() {
		return channel;
	}

        /**
         *
         * @param channel number of selected channel
         * CHANNEL_NULL when no channel is selected
         */
	public void setChannel(int channel) {
		this.channel = ((type != SignalSelectionType.CHANNEL || channel < 0) ? CHANNEL_NULL : channel);
	}

        /**
         * Assuming that segment has a given size finds that one where selection starts
         * @param segmentSize size of a segment
         * @return number of the segment where selection starts
         */
	public int getStartSegment(float segmentSize) {
		return (int)(position / segmentSize);
	}

        /**
         * Assuming that segment has a given size finds that one where selection ends
         * @param segmentSize size of a segment
         * @return number of the segment where selection ends
         */
        //TODO co to faktycznie zwraca? - moim zdaniem komentarz poniżej jest błędny
	// this is exclusive (returns first segment after the segment in which the selection ends)
	public int getEndSegment(float segmentSize) {
		return (int)((position+length) / segmentSize);
	}

        /**
         * Computes how many segments of given size would fit in selection
         * @param segmentSize size of a segment
         * @return length of selection in segments
         */
	public int getSegmentLength(float segmentSize) {
		return (int)(length / segmentSize);
	}

        /**
         * Compares the current SignalSelection object with another SignalSelection object.
         * @param s the SignalSelection that the current SignalSelection is to be compared with.
         * @return true if the two SignalSelection objects are equal, otherwise false.
         */
	public boolean equals(SignalSelection s) {
		if (type != s.type) {
			return false;
		}
		if (position != s.position) {
			return false;
		}
		if (length != s.length) {
			return false;
		}
		if (channel != channel) {
			return false;
		}
		return true;
	}

        /**
         * Compares the current SignalSelection object with another object. If obj is not of type SignalSelection false is returned
         * @param obj the object that the current SignalSelection is to be compared with.
         * @return true if the two objects are equal, otherwise false.
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SignalSelection)) {
			return false;
		}
		return equals((SignalSelection) obj);
	}

        /**
         * Checks if intersection of current SignalSelection with another SignalSelection is nonempty
         * @param selection the SignalSelection that the current SignalSelection is to be intersect with
         * @return true if the two SignalSelection objects overlap, otherwise false.
         */
	public boolean overlaps(SignalSelection selection) {

		float sEndPosition = selection.position + selection.length;
		if (selection.position <= position && sEndPosition <= position) {
			return false;
		}
		float endPosition = position + length;
		if (selection.position >= endPosition && sEndPosition >= endPosition) {
			return false;
		}

		return true;

	}

}
