/**
 * 
 */
package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginCodecEvent;

/**
 * Implementation of {@link PluginCodecEvent}.
 * Contains the format name of the codec.
 * @author Marcin Szumski
 */
public class PluginCodecEventImpl implements PluginCodecEvent {

	/**
	 * the format name of the codec associated with this event
	 */
	protected String formatName = null;
	
	/**
	 * Constructor. Sets the format name of the codec associated with this
	 * event.
	 * @param codecFormatName the format name to set
	 */
	public PluginCodecEventImpl(String codecFormatName) {
		formatName = codecFormatName;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.CodecEvent#getCodecFormatName()
	 */
	@Override
	public String getCodecFormatName() {
		return formatName;
	}

}
