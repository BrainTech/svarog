/**
 * 
 */
package org.signalml.plugin.impl.change;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.TagTreeModel;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagEvent;
import org.signalml.domain.tag.TagListener;
import org.signalml.domain.tag.TagStyleEvent;
import org.signalml.domain.tag.TagStyleListener;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.SvarogTagEvent;
import org.signalml.plugin.export.change.SvarogTagListener;
import org.signalml.plugin.export.change.SvarogTagStyleEvent;
import org.signalml.plugin.export.change.SvarogTagStyleListener;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * A part of the implementation of {@link SvarogAccessChangeSupport}.
 * It listens for changes concerning {@link Tag tags} and {@link TagStyle tag
 * styles} (addition, removal and change).
 * adding/removing/changing of a tag/tag style.
 * For every type of a change holds the list of plug-ins listening for it
 * and informs them when this change occurs.
 * <p>
 * This is used both as a super-type for {@link ChangeSupportImpl} and to listen
 * for changes associated with a single {@link TagDocument tag}/{@link
 * SignalDocument signal} document.
 * 
 * @author Marcin Szumski
 */
public class ChangeSupportDocumentImpl implements TagListener, TagStyleListener {

	protected static final Logger logger = Logger.getLogger(ChangeSupportImpl.class);
	
	/**
	 * {@link SvarogTagListener listeners} on {@link ExportedTag tag} changes
	 * (addition, removal, change)
	 */
	protected ArrayList<SvarogTagListener> tagListeners = new ArrayList<SvarogTagListener>();
	
	/**
	 * {@link SvarogTagStyleListener listener} on {@link ExportedTagStyle
	 * tag style} changes (addition, removal, change)
	 */
	protected ArrayList<SvarogTagStyleListener> tagStyleListeners = new ArrayList<SvarogTagStyleListener>();
	
	/**
	 * the manager of elements of Svarog
	 */
	protected ViewerElementManager manager;
	
	/**
	 * Constructor.
	 */
	public ChangeSupportDocumentImpl(){
	}
	
	/**
	 * Adds a {@link SvarogTagListener} to the list of tag listeners.
	 * @param listener the listener to add
	 */
	public void addTagListener(SvarogTagListener listener){
		tagListeners.add(listener);
	}
	
	/**
	 * Adds a {@link SvarogTagStyleListener} to the list of tag listeners.
	 * @param listener the listener to add
	 */
	public void addTagStyleListener(SvarogTagStyleListener listener){
		tagStyleListeners.add(listener);
	}
	
	/**
	 * Creates a {@link SvarogTagEvent} from given {@link TagEvent}.
	 * Sets the tag and document in which the tag is located.
	 * @param e the TagEvent to be used
	 * @return created SvarogTagEvent
	 */
	protected SvarogTagEvent createTagEvent(TagEvent e){
		Tag tag = e.getTag();
		TagDocument document = null;
		if (e.getSource() instanceof StyledTagSet){
			StyledTagSet tagSet = (StyledTagSet) e.getSource();
			TagTreeModel treeModel = manager.getTagTreeModel();
			document = treeModel.getDocumentFromSet(tagSet);
		}
		TagEventImpl tagEvent = new TagEventImpl(tag, document);
		return tagEvent;
	}
	
	/**
	 * Informs given {@link SvarogTagListener listener} that the tag was added.
	 * @param event the {@link SvarogTagEvent event} describing the change
	 * @param listener the listener to be informed
	 */
	protected void singleTagAdded(SvarogTagEvent event, SvarogTagListener listener){
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
			SvarogTagEvent event = createTagEvent(e);
			for (SvarogTagListener listener: tagListeners){
				singleTagAdded(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was added");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Informs given {@link SvarogTagListener listener} that the tag was removed.
	 * @param event the {@link SvarogTagEvent event} describing the change
	 * @param listener the listener to be informed
	 */
	protected void singleTagRemoved(SvarogTagEvent event, SvarogTagListener listener){
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
			SvarogTagEvent event = createTagEvent(e);
			for (SvarogTagListener listener: tagListeners){
				singleTagRemoved(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was removed");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Informs given {@link SvarogTagListener listener} that the tag was changed.
	 * @param event the {@link SvarogTagEvent event} describing the change
	 * @param listener the listener to be informed
	 */
	protected void singleTagChanged(SvarogTagEvent event, SvarogTagListener listener){
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
			SvarogTagEvent event = createTagEvent(e);
			for (SvarogTagListener listener: tagListeners){
				singleTagChanged(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag has changed");
			ex.printStackTrace();
		}
	}

	/**
	 * Creates a {@link SvarogTagStyleEvent} from given {@link TagStyleEvent}.
	 * Sets the style.
	 * @param e the TagStyleEvent to be used
	 * @return created SvarogTagStyleEvent
	 */
	protected SvarogTagStyleEvent createTagStyleEvent(TagStyleEvent e){
		TagStyle style = e.getTagStyle();
		TagStyleEventImpl tagStyleEvent = new TagStyleEventImpl(style);
		return tagStyleEvent;
	}
	
	/**
	 * Informs listeners that a {@link TagStyle} was added.
	 * @param e the event object which describes a change
	 */
	@Override
	public void tagStyleAdded(TagStyleEvent e) {
		try {
			SvarogTagStyleEvent event = createTagStyleEvent(e);
			for (SvarogTagStyleListener listener: tagStyleListeners){
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
			SvarogTagStyleEvent event = createTagStyleEvent(e);
			for (SvarogTagStyleListener listener: tagStyleListeners){
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
			SvarogTagStyleEvent event = createTagStyleEvent(e);
			for (SvarogTagStyleListener listener: tagStyleListeners){
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
	
	/**
	 * Sets the element manager, stores active documents and
	 * adds listeners for codec manager and document manager. 
	 * @param elementManager the element manager to set
	 */
	public void setManager(ViewerElementManager elementManager){
		manager = elementManager;
	}
}
