package org.signalml.domain.tag;

import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public class MonitorTag extends Tag {

	private static final long serialVersionUID = 1L;

	protected StyledMonitorTagSet parent;

	public MonitorTag(TagStyle style, double position, double length,
		int channel) {
		super(style, position, length, channel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getPosition() {
		return this.parent.computePosition(this.position);
	}

	public double getRealPosition() {
		return this.position;
	}

	public void setParent(StyledMonitorTagSet parent) {
		this.parent = parent;
	}

}
