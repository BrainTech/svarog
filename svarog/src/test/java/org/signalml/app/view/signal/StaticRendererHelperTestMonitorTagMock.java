package org.signalml.app.view.signal;

import static org.signalml.app.view.signal.StaticRenderingHelperTest.PAGE_SIZE;
import org.signalml.domain.tag.*;
import org.signalml.plugin.export.signal.TagStyle;

public class StaticRendererHelperTestMonitorTagMock extends MonitorTag {

	private double now;

	public StaticRendererHelperTestMonitorTagMock(double timestamp, double length) {
		super(TagStyle.getDefaultChannel(), timestamp, length, 0, "");
		setParent(new StyledMonitorTagSet((float) PAGE_SIZE, 1));
	}

	@Override
	public double getEndPosition() {
		if (isComplete()) {
			return getPosition() + length;
		} else {
			return this.parent.computePosition(now);
		}
	}

	@Override
	public double getLength() {
		if (isComplete()) {
			return length;
		} else {
			return now - getTimestamp();
		}
	}

	public void setNow(double now) {
		this.now = now;
		parent.newSample(now);
	}
}
