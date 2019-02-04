package org.signalml.plugin.data.tag;

public class PluginChannelTagDef implements IPluginTagDef {

	private final double offset;
	private final double length;
	private final int channel;

	public PluginChannelTagDef(double offset, double length, int channel) {
		this.offset = offset;
		this.length = length;
		this.channel = channel;
	}

	@Override
	public double getOffset() {
		return this.offset;
	}

	@Override
	public double getLength() {
		return this.length;
	}

	@Override
	public int getChannel() {
		return this.channel;
	}



}
