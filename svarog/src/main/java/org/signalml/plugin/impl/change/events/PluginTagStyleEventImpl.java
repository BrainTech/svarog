/**
 *
 */
package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginTagStyleEvent;
import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Implementation of {@link PluginTagStyleEvent}.
 * Contains the {@link ExportedTagStyle tag style}.
 * @author Marcin Szumski
 */
public class PluginTagStyleEventImpl implements PluginTagStyleEvent {

	/**
	 * the tag style associated with this event
	 */
	protected ExportedTagStyle style;

	/**
	 * Constructor.
	 * @param style the {@link ExportedTagStyle tag style} to be associated
	 * with this event
	 */
	public PluginTagStyleEventImpl(ExportedTagStyle style) {
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
