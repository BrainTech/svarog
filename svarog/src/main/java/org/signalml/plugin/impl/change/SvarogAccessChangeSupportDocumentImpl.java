/**
 * 
 */
package org.signalml.plugin.impl.change;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.tag.TagTreeModel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagEvent;
import org.signalml.domain.tag.TagListener;
import org.signalml.domain.tag.TagStyleEvent;
import org.signalml.domain.tag.TagStyleListener;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.events.PluginTagEvent;
import org.signalml.plugin.export.change.events.PluginTagStyleEvent;
import org.signalml.plugin.export.change.listeners.PluginTagListener;
import org.signalml.plugin.export.change.listeners.PluginTagStyleListener;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.impl.AbstractAccess;
import org.signalml.plugin.impl.PluginAccessClass;
import org.signalml.plugin.impl.change.events.PluginTagEventImpl;
import org.signalml.plugin.impl.change.events.PluginTagStyleEventImpl;

/**
 * A part of the implementation of {@link SvarogAccessChangeSupport}.
 * It listens for changes concerning {@link Tag tags} and {@link TagStyle tag
 * styles} (addition, removal and change).
 * adding/removing/changing of a tag/tag style.
 * For every type of a change holds the list of plug-ins listening for it
 * and informs them when this change occurs.
 * <p>
 * This is used both as a super-type for {@link SvarogAccessChangeSupportImpl} and to listen
 * for changes associated with a single {@link TagDocument tag}/{@link
 * SignalDocument signal} document.
 * 
 * @author Marcin Szumski
 */
public class SvarogAccessChangeSupportDocumentImpl extends AbstractAccess implements TagListener, TagStyleListener {

	protected static final Logger logger = Logger.getLogger(SvarogAccessChangeSupportImpl.class);
	
	/**
	 * {@link PluginTagListener listeners} on {@link ExportedTag tag} changes
	 * (addition, removal, change)
	 */
	protected ArrayList<PluginTagListener> tagListeners = new ArrayList<PluginTagListener>();
	
	/**
	 * {@link PluginTagStyleListener listener} on {@link ExportedTagStyle
	 * tag style} changes (addition, removal, change)
	 */
	protected ArrayList<PluginTagStyleListener> tagStyleListeners = new ArrayList<PluginTagStyleListener>();
	
	protected SvarogAccessChangeSupportDocumentImpl() { }

	/**
	 * Adds a {@link PluginTagListener} to the list of tag listeners.
	 * @param listener the listener to add
	 */
	public void addTagListener(PluginTagListener listener){
		tagListeners.add(listener);
	}
	
	/**
	 * Adds a {@link PluginTagStyleListener} to the list of tag listeners.
	 * @param listener the listener to add
	 */
	public void addTagStyleListener(PluginTagStyleListener listener){
		tagStyleListeners.add(listener);
	}
	
	/**
	 * Creates a {@link PluginTagEvent} from given {@link TagEvent}.
	 * Sets the tag and document in which the tag is located.
	 * @param e the TagEvent to be used
	 * @return created SvarogTagEvent
	 */
	protected PluginTagEvent createTagEvent(TagEvent e){
		Tag tag = e.getTag();
		TagDocument document = null;
		if (e.getSource() instanceof StyledTagSet){
			StyledTagSet tagSet = (StyledTagSet) e.getSource();
			TagTreeModel treeModel = getViewerElementManager().getTagTreeModel();
			document = treeModel.getDocumentFromSet(tagSet);
		}
		PluginTagEventImpl tagEvent = new PluginTagEventImpl(tag, document);
		return tagEvent;
	}
	
	/**
	 * Informs given {@link PluginTagListener listener} that the tag was added.
	 * @param event the {@link PluginTagEvent event} describing the change
	 * @param listener the listener to be informed
	 */
	protected void singleTagAdded(PluginTagEvent event, PluginTagListener listener){
		try {
			listener.tagAdded(event);
		} catch (Exception ex) {
			logger.error("unhandled exception in plugin on tag added");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Informs listeners that a {@link Tag} was added.
	 * @param e the event object which describes a change
	 */
	@Override
	public void tagAdded(TagEvent e) {
		try {
			PluginTagEvent event = createTagEvent(e);
			for (PluginTagListener listener: tagListeners){
				singleTagAdded(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was added");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Informs given {@link PluginTagListener listener} that the tag was removed.
	 * @param event the {@link PluginTagEvent event} describing the change
	 * @param listener the listener to be informed
	 */
	protected void singleTagRemoved(PluginTagEvent event, PluginTagListener listener){
		try {
			listener.tagRemoved(event);
		} catch (Exception ex) {
			logger.error("unhandled exception in plugin on tag removed");
			ex.printStackTrace();
		}
	}

	/**
	 * Informs listeners that a {@link Tag} was removed.
	 * @param e the event object which describes a change
	 */
	@Override
	public void tagRemoved(TagEvent e) {
		try {
			PluginTagEvent event = createTagEvent(e);
			for (PluginTagListener listener: tagListeners){
				singleTagRemoved(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was removed");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Informs given {@link PluginTagListener listener} that the tag was changed.
	 * @param event the {@link PluginTagEvent event} describing the change
	 * @param listener the listener to be informed
	 */
	protected void singleTagChanged(PluginTagEvent event, PluginTagListener listener){
		try {
			listener.tagChanged(event);
		} catch (Exception ex) {
			logger.error("unhandled exception in plugin on tag changed");
			ex.printStackTrace();
		}
	}

	/**
	 * Informs listeners that a {@link Tag} was changed.
	 * @param e the event object which describes a change
	 */
	@Override
	public void tagChanged(TagEvent e) {
		try {
			PluginTagEvent event = createTagEvent(e);
			for (PluginTagListener listener: tagListeners){
				singleTagChanged(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag has changed");
			ex.printStackTrace();
		}
	}

	/**
	 * Creates a {@link PluginTagStyleEvent} from given {@link TagStyleEvent}.
	 * Sets the style.
	 * @param e the TagStyleEvent to be used
	 * @return created SvarogTagStyleEvent
	 */
	protected PluginTagStyleEvent createTagStyleEvent(TagStyleEvent e){
		TagStyle style = e.getTagStyle();
		PluginTagStyleEventImpl tagStyleEvent = new PluginTagStyleEventImpl(style);
		return tagStyleEvent;
	}
	
	/**
	 * Informs listeners that a {@link TagStyle} was added.
	 * @param e the event object which describes a change
	 */
	@Override
	public void tagStyleAdded(TagStyleEvent e) {
		try {
			PluginTagStyleEvent event = createTagStyleEvent(e);
			for (PluginTagStyleListener listener: tagStyleListeners){
				try {
					listener.tagStyleAdded(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on tag style addded");
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag style was added");
			ex.printStackTrace();
		}
	}

	/**
	 * Informs listeners that a {@link TagStyle} was removed.
	 * @param e the event object which describes a change
	 */
	@Override
	public void tagStyleRemoved(TagStyleEvent e) {
		try {
			PluginTagStyleEvent event = createTagStyleEvent(e);
			for (PluginTagStyleListener listener: tagStyleListeners){
				try {
					listener.tagStyleRemoved(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on tag style removed");
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag style was removed");
			ex.printStackTrace();
		}
	}

	/**
	 * Informs listeners that a {@link TagStyle} was changed.
	 * @param e the event object which describes a change
	 */
	@Override
	public void tagStyleChanged(TagStyleEvent e) {
		try {
			PluginTagStyleEvent event = createTagStyleEvent(e);
			for (PluginTagStyleListener listener: tagStyleListeners){
				try {
					listener.tagStyleChanged(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on tag style changed");
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag style was added");
			ex.printStackTrace();
		}
	}

    public void setViewerElementManager(ViewerElementManager manager) {
        super.setViewerElementManager(manager);
    }

}
