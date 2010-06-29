/* SignalSelection.java created 2007-10-03
 *
 */

package org.signalml.domain.signal;

import java.io.Serializable;

/** SignalSelection
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelection implements Serializable {

	public static final int CHANNEL_NULL = -1;

	private static final long serialVersionUID = 1L;

	protected SignalSelectionType type;

	protected float position;
	protected float length;

	protected int channel;

	public SignalSelection(SignalSelectionType type) {
		if (type == null) {
			throw new NullPointerException("No type");
		}
		this.type = type;
		this.channel = CHANNEL_NULL;
	}

	public SignalSelection(SignalSelectionType type, float position, float length) {
		if (type == null) {
			throw new NullPointerException("No type");
		}
		this.type = type;
		this.position = position;
		this.length = length;
		this.channel = CHANNEL_NULL;
	}

	public SignalSelection(SignalSelectionType type, float position, float length, int channel) {
		this.type = type;
		this.position = position;
		this.length = length;
		this.channel = ((type != SignalSelectionType.CHANNEL || channel < 0) ? CHANNEL_NULL : channel);
	}

	public SignalSelection(SignalSelection selection) {
		this.position = selection.position;
		this.length = selection.length;
		this.channel = selection.channel;
		this.type = selection.type;
	}

	public void setParameters(float position, float length, int channel) {
		this.position = position;
		this.length = length;
		this.channel = ((type != SignalSelectionType.CHANNEL || channel < 0) ? CHANNEL_NULL : channel);
	}

	public void setParameters(float position, float length) {
		this.position = position;
		this.length = length;
	}

	public SignalSelectionType getType() {
		return type;
	}

	public float getPosition() {
		return position;
	}

	public void setPosition(float position) {
		this.position = position;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getCenterPosition() {
		return position + length / 2;
	}

	public float getEndPosition() {
		return position + length;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = ((type != SignalSelectionType.CHANNEL || channel < 0) ? CHANNEL_NULL : channel);
	}

	public int getStartSegment(float segmentSize) {
		return (int)(position / segmentSize);
	}

	// this is exclusive (returns first segment after the segment in which the selection ends)
	public int getEndSegment(float segmentSize) {
		return (int)((position+length) / segmentSize);
	}

	public int getSegmentLength(float segmentSize) {
		return (int)(length / segmentSize);
	}

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SignalSelection)) {
			return false;
		}
		return equals((SignalSelection) obj);
	}

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
