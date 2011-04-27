/**
 * 
 */
package org.signalml.plugin.impl.change;

import org.signalml.plugin.export.change.SvarogCodecEvent;

/**
 * Implementation of {@link SvarogCodecEvent}.
 * Contains the format name of the codec.
 * @author Marcin Szumski
 */
public class CodecEventImpl implements SvarogCodecEvent {

	/**
	 * the format name of the codec associated with this event
	 */
	protected String formatName = null;
	
	/**
	 * Constructor. Sets the format name of the codec associated with this
	 * event.
	 * @param codecFormatName the format name to set
	 */
	public CodecEventImpl(String codecFormatName) {
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
