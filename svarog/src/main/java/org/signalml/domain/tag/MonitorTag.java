package org.signalml.domain.tag;

import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public class MonitorTag extends Tag {

	private static final long serialVersionUID = 1L;

	protected StyledMonitorTagSet parent;
	private double timestamp;

	public MonitorTag(TagStyle style, double timestamp, double length,
					  int channel) {
		super(style, 0, length, channel); //the position argument doesn't realy matter
		this.timestamp = timestamp;
	}

	@Override
	public double getPosition() {
		return this.parent.computePosition(this.timestamp);
	}

	@Override
	public double getTimestamp() {
		return this.timestamp;
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

}
