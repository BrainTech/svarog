package org.signalml.domain.tag;

import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public class MonitorTag extends Tag {

	private static final long serialVersionUID = 1L;

	protected StyledMonitorTagSet parent;
	private final double timestamp;
	private final String id;

	public MonitorTag(TagStyle style, double timestamp, double length,
					  int channel, String id) {
		super(style, 0, length, channel); //the position argument doesn't realy matter
		this.timestamp = timestamp;
		this.id = id;
	}

	public boolean isComplete() {
		return !Double.isInfinite(length);
	}

	@Override
	public double getPosition() {
		return this.parent.computePosition(this.timestamp);
	}

	@Override
	public double getTimestamp() {
		return this.timestamp;
	}

	public String getID() {
		return this.id;
	}

	@Override
	public double getEndPosition() {
		if (isComplete()) {
			return getPosition() + length;
		} else {
			return getCurrentTimestamp();
		}
	}

	@Override
	public double getLength() {
		if (isComplete()) {
			return length;
		} else {
			return getCurrentTimestamp() - getPosition();
		}
	}

	public void setParent(StyledMonitorTagSet parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MonitorTag)) {
			return false;
		}
		return (this.compareTo((Tag) obj) == 0);
	}

	public static double getCurrentTimestamp() {
		return 0.001 * System.currentTimeMillis();
	}
}
