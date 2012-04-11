/**
 * 
 */
package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginActiveTagEvent;
import org.signalml.plugin.export.signal.ExportedTag;

/**
 * Implementation of {@link PluginActiveTagEvent}.
 * Contains the old and new value of the active {@link ExportedTag tag}.
 * @author Marcin Szumski
 */
public class PluginActiveTagEventImpl implements PluginActiveTagEvent {

	/**
	 * the old value of the active {@link ExportedTag tag}
	 */
	protected ExportedTag oldTag;
	/**
	 * the new value of the active {@link ExportedTag tag}
	 */
	protected ExportedTag tag;
	
	/**
	 * Constructor. Sets the old and new value of the active
	 * {@link ExportedTag tag}.
	 * @param tag the old value of the active tag
	 * @param oldTag the new value of the active tag
	 */
	public PluginActiveTagEventImpl(ExportedTag tag, ExportedTag oldTag) {
		this.tag = tag;
		this.oldTag = oldTag;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.TagEvent#getTag()
	 */
	@Override
	public ExportedTag getTag() {
		return tag;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.ActiveTagEvent#getOldTag()
	 */
	@Override
	public ExportedTag getOldTag() {
		return oldTag;
	}

}
