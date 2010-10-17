/**
 * 
 */
package org.signalml.plugin.impl.change;

import org.signalml.plugin.export.change.SvarogTagStyleEvent;
import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Implementation of {@link SvarogTagStyleEvent}.
 * Contains the {@link ExportedTagStyle tag style}.
 * @author Marcin Szumski
 */
public class TagStyleEventImpl implements SvarogTagStyleEvent {

	/**
	 * the tag style associated with this event
	 */
	protected ExportedTagStyle style;
	
	/**
	 * Constructor.
	 * @param style the {@link ExportedTagStyle tag style} to be associated
	 * with this event
	 */
	public TagStyleEventImpl(ExportedTagStyle style){
		this.style = style;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.TagStyleEvent#getTagStyle()
	 */
	@Override
	public ExportedTagStyle getTagStyle() {
		return style;
	}

}
