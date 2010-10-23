package org.signalml.domain.tag;

import org.apache.log4j.Logger;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public class MonitorTag extends Tag {
	protected StyledMonitorTagSet parent;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
