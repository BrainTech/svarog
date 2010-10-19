/**
 * 
 */
package org.signalml.plugin.impl.change;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SignalView;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecManagerEvent;
import org.signalml.codec.SignalMLCodecManagerListener;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagEvent;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.SvarogCloseListener;
import org.signalml.plugin.export.change.SvarogCodecEvent;
import org.signalml.plugin.export.change.SvarogCodecListener;
import org.signalml.plugin.export.change.SvarogDocumentEvent;
import org.signalml.plugin.export.change.SvarogDocumentListener;
import org.signalml.plugin.export.change.SvarogTagDocumentListener;
import org.signalml.plugin.export.change.SvarogTagEvent;
import org.signalml.plugin.export.change.SvarogTagListener;
import org.signalml.plugin.export.change.SvarogTagListenerWithAcitve;
import org.signalml.plugin.export.change.SvarogTagStyleListener;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.view.DocumentView;


/**
 * Implementation of {@link SvarogAccessChangeSupport}.
 * For every type of a change holds the list of plug-ins listening for it
 * and informs them when this change occurs.
 * <p>
 * Remembers:
 * <ul>
 * <li>the active {@link Document} and {@link TagDocument},</li>
 * <li>the active tag for every view</li>
 * </ul>
 * to be able to pass the old and new value of the active elements to listeners.
 * <p>
 * For every tag and {@link SignalDocument signal document} holds the
 * {@link ChangeSupportDocumentImpl document change support}. 
 * <p>
 * To listen on every change:
 * <ul>
 * <li>when an {@link ViewerElementManager element manager} is
 * {@link #setManager(ViewerElementManager) set}:
 * <ul>
 * <li>{@link DocumentManager#addDocumentManagerListener(DocumentManagerListener)
 * registers} listening for addition/removal of a document</li>
 * <li>{@link SignalMLCodecManager#addSignalMLCodecManagerListener(SignalMLCodecManagerListener)
 * registers} listening for addition/removal of a {@link SignalMLCodec codec}</li>
 * <li>{@link ActionFocusManager#addActionFocusListener(ActionFocusListener)
 * registers} listening for active document/tag document changes</li>
 * </ul>
 * </li>
 * <li>when document {@link #documentAdded(DocumentManagerEvent) is added}:
 * <ul>
 * <li>if it is signal or tag document registers listening for
 * {@link StyledTagSet#addTagListener(org.signalml.domain.tag.TagListener) tag}
 * and 
 * {@link StyledTagSet#addTagStyleListener(org.signalml.domain.tag.TagStyleListener)
 * tag style} changes</li>
 * <li>if it is a signal document and has a view
 * {@link #registerFocusListener(DocumentView) registers} listening for
 * active tag changes</li>
 * <li>{@link Document#addPropertyChangeListener(PropertyChangeListener)
 * registers} listening for {@link DocumentView} changes</li>
 * <li>if it is signal or tag document creates a
 * {@link ChangeSupportDocumentImpl document change support} and registers
 * it to listen for tag and tag style changes associated with this document</li>
 * </ul></li>
 * <li>when a document view for a document
 * {@link #propertyChange(PropertyChangeEvent) changed} and it is a SignalView:
 * <ul><li>{@link #registerFocusListener(DocumentView) registers} listening for
 * active tag changes</li></ul>
 * </ul>
 * 
 * @author Marcin Szumski
 */
public class ChangeSupportImpl extends ChangeSupportDocumentImpl implements SvarogAccessChangeSupport, ActionFocusListener, DocumentManagerListener, SignalMLCodecManagerListener, PropertyChangeListener{
	
	/**
	 * the currently active document
	 */
	private Document activeDocument = null;
	/**
	 * the currently active tag document
	 */
	private TagDocument activeTagDocument = null;
	
	/**
	 * {@link SvarogCloseListener listeners} on close of Svarog
	 */
	private ArrayList<SvarogCloseListener> closeListeners = new ArrayList<SvarogCloseListener>();
	
	/**
	 * {@link SvarogCodecListener listeners} on codec changes (addition
	 * and removal)
	 */
	private ArrayList<SvarogCodecListener> codecListeners = new ArrayList<SvarogCodecListener>();
	
	/**
	 * {@link SvarogDocumentListener listeners} on changes associated
	 * with a {@link Document}
	 */
	private ArrayList<SvarogDocumentListener> documentListeners = new ArrayList<SvarogDocumentListener>();
	
	/**
	 * {@link SvarogTagDocumentListener listeners} on changes of
	 * an active {@link TagDocument}
	 */
	private ArrayList<SvarogTagDocumentListener> tagDocumentListeners = new ArrayList<SvarogTagDocumentListener>();
	
	/**
	 * {@link SvarogTagListenerWithAcitve listeners} on {@link ExportedTag tag}
	 * changes (addition, removal, change) including changes of an active tag
	 */
	protected ArrayList<SvarogTagListenerWithAcitve> tagListenersWithActive = new ArrayList<SvarogTagListenerWithAcitve>();
	
	/**
	 * HashMap associating signal documents with {@link ChangeSupportDocumentImpl listeners} for them
	 */
	private HashMap<ExportedSignalDocument, ChangeSupportDocumentImpl> listenersOnSignalDocument = new HashMap<ExportedSignalDocument, ChangeSupportDocumentImpl>();
	
	/**
	 * HashMap associating tag documents with {@link ChangeSupportDocumentImpl listeners} for them
	 */
	private HashMap<ExportedTagDocument, ChangeSupportDocumentImpl> listenersOnTagDocument = new HashMap<ExportedTagDocument, ChangeSupportDocumentImpl>();
	
	
	
	/**
	 * HashMap associating signal views with tags active in them
	 */
	private HashMap<SignalView, Tag> activeTags = new HashMap<SignalView, Tag>();
	
	
	/**
	 * Constructor.
	 */
	public ChangeSupportImpl(){		
	}
		
	/**
	 * Informs listeners that focus changed, that is:
	 * <ul>
	 * <li>active document changed</li>
	 * <li>active tag document change</li>
	 * <li>active tag changed</li>
	 * </ul>
	 * @param e an event associated with a change
	 */
	@Override
	public void actionFocusChanged(ActionFocusEvent e) {
		try{
			ActionFocusManager focusManager = e.getActionFocusManager();
			if (focusManager != null){
				Document currentActiveDocument = focusManager.getActiveDocument();
				if (currentActiveDocument != activeDocument){
					ActiveDocumentEventImpl event = new ActiveDocumentEventImpl(currentActiveDocument, activeDocument);
					activeDocument = currentActiveDocument;
					for (SvarogDocumentListener listener : documentListeners){
						try {
							listener.activeDocumentChanged(event);
						} catch (Exception ex) {
							logger.error("unhandled exception in plugin on active document change");
							ex.printStackTrace();
						}
					}
				} else {
					TagDocument currentActiveTagDocument = focusManager.getActiveTagDocument();
					if (currentActiveTagDocument != activeTagDocument){
						TagDocumentEventImpl event = new TagDocumentEventImpl(currentActiveTagDocument, activeTagDocument);
						activeTagDocument = currentActiveTagDocument;
						for (SvarogTagDocumentListener listener : tagDocumentListeners){
							try {
								listener.activeTagDocumentChanged(event);
							} catch (Exception ex) {
								logger.error("unhandled exception in plugin on active tag document change");
								ex.printStackTrace();
							}
						}
					}
				}
				
			} else {
				if (e.getSource() instanceof SignalView){
					SignalView signalView = (SignalView) e.getSource();
					PositionedTag positionedTag = signalView.getActiveTag();
					Tag newActiveTag;
					if (positionedTag != null)
						newActiveTag = positionedTag.getTag();
					else newActiveTag = null;
					ExportedTag oldActiveTag = activeTags.get(signalView);
					if (oldActiveTag != newActiveTag){
						ActiveTagEventImpl event = new ActiveTagEventImpl(newActiveTag, oldActiveTag);
						for (SvarogTagListenerWithAcitve listener : tagListenersWithActive){
							try {
								listener.activeTagChanged(event);
							} catch (Exception ex) {
								logger.error("unhandled exception in plugin on active tag change");
								ex.printStackTrace();
							}
						}
						activeTags.put(signalView, newActiveTag);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when action focus changed");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Adds provided listener and the this listener to the {@link StyledTagSet set}
	 * associated with a given {@link TagDocument}.  
	 * @param tagDocument the tag document
	 * @param listener the listener to be added
	 */
	private void addListenersForTagDocument(TagDocument tagDocument, ChangeSupportDocumentImpl listener){
		if (listenersOnTagDocument.get(tagDocument) != null) return;
		StyledTagSet tagSet = tagDocument.getTagSet();
		SignalDocument parent = tagDocument.getParent();
		if (tagSet == null) return;
		tagSet.removeTagListener(this);
		tagSet.removeTagStyleListener(this);
		tagSet.removeTagListener(listener);
		tagSet.removeTagStyleListener(listener);
		
		tagSet.addTagListener(this);
		tagSet.addTagListener(listener);
		tagSet.addTagStyleListener(this);
		tagSet.addTagStyleListener(listener);
		ChangeSupportDocumentImpl parentListener = listenersOnSignalDocument.get(parent);
		if (parentListener != null){
			tagSet.removeTagListener(parentListener);
			tagSet.removeTagStyleListener(parentListener);
			tagSet.addTagListener(parentListener);
			tagSet.addTagStyleListener(parentListener);
		}
		
		listenersOnTagDocument.put(tagDocument, listener);
	}
	
	/**
	 * Adds listeners on tag changes for a given document.
	 * If document is of type {@link TagDocument} calls 
	 * {@link #addListenersForTagDocument(TagDocument, ChangeSupportDocumentImpl)} once,
	 * if document is of type {@link SignalDocument} adds listeners for all
	 * dependent tag documents.
	 * @param e an event associated with a change
	 */
	private void addListenersForDocument(DocumentManagerEvent e){
		Document document = e.getDocument();
		if (document ==  null) return;
		
		document.addPropertyChangeListener(this);
		
		DocumentView documentView = document.getDocumentView();
		if (documentView != null){
			if (documentView instanceof SignalView){
				registerFocusListener(documentView);
			}
		}
		
		
		if (document instanceof TagDocument || document instanceof SignalDocument){
			ChangeSupportDocumentImpl tagDocumentListener = new ChangeSupportDocumentImpl();
			tagDocumentListener.setManager(manager);
			
			if (document instanceof TagDocument){
				TagDocument tagDocument = (TagDocument) document;
				addListenersForTagDocument(tagDocument, tagDocumentListener);
			} else {
				SignalDocument signalDocument = (SignalDocument) document;
				if (listenersOnSignalDocument.get(signalDocument) == null){
					listenersOnSignalDocument.put(signalDocument, tagDocumentListener);
					List<TagDocument> tagDocuments = signalDocument.getTagDocuments();
					for (TagDocument tagDocument : tagDocuments){
						StyledTagSet tagSet = tagDocument.getTagSet();
						tagSet.removeTagListener(this);
						tagSet.removeTagStyleListener(this);
						
						tagSet.addTagListener(this);
						tagSet.addTagListener(tagDocumentListener);
						tagSet.addTagStyleListener(this);
						tagSet.addTagStyleListener(tagDocumentListener);
					}
				}
			}

		}
	}
	
	/**
	 * Creates a {@link SvarogDocumentEvent} from given
	 * {@link DocumentManagerEvent}.
	 * Sets the document.
	 * @param e the DocumentManagerEvent to be used
	 * @return created SvarogDocumentEvent
	 */
	private SvarogDocumentEvent createDocumentEvent(DocumentManagerEvent e){
		Document document = e.getDocument();
		DocumentEventImpl event = new DocumentEventImpl(document);
		return event;
	}
	
	/**
	 * Informs listeners that the document was added and registers listeners
	 * for the added document.
	 * @param e an event associated with a change
	 */
	@Override
	public void documentAdded(DocumentManagerEvent e) {
		try {
			addListenersForDocument(e);
			SvarogDocumentEvent event = createDocumentEvent(e);
			for (SvarogDocumentListener listener : documentListeners){
				try {
					listener.documentAdded(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on document added");
					ex.printStackTrace();
				}
				
			}
		} catch (Exception e2) {
			logger.error("Unknown error in plug-in interface when document was added");
			e2.printStackTrace();
		}
	}

	/**
	 * Informs listeners that the document was removed.
	 * @param e an event associated with a change
	 */
	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		try {
			SvarogDocumentEvent event = createDocumentEvent(e);
			for (SvarogDocumentListener listener : documentListeners){
				try {
					listener.documentRemoved(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on document removed");
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when document was removed");
			ex.printStackTrace();
		}
	}

	/**
	 * Does nothing.
	 * @param e an event associated with a change
	 */
	public void documentPathChanged(DocumentManagerEvent e) {
		//nothing to do
	}

	/**
	 * Creates a {@link SvarogCodecEvent} from given
	 * {@link SignalMLCodecManagerEvent}.
	 * Sets the format name of the codec.
	 * @param ev the SignalMLCodecManagerEvent to be used
	 * @return created SvarogCodecEvent
	 */
	private SvarogCodecEvent createCodecEvent(SignalMLCodecManagerEvent ev){
		SignalMLCodec codec = ev.getCodec();
		String formatName = null;
		if (codec == null) throw new RuntimeException("no codec in the SignalMLCodecManagerEvent");
		formatName = codec.getFormatName();
		CodecEventImpl event = new CodecEventImpl(formatName);
		return event;
	}
	
	
	/**
	 * Informs listeners that a codec was added.
	 * @param ev an event describing this change 
	 */
	@Override
	public void codecAdded(SignalMLCodecManagerEvent ev) {
		try {
			SvarogCodecEvent event = createCodecEvent(ev);	
			for (SvarogCodecListener listener : codecListeners){
				try {
					listener.codecAdded(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on codec added");
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface when codec was added");
			e.printStackTrace();
		}
	}

	/**
	 * Informs listeners that a codec was removed.
	 * @param ev an event describing this change 
	 */
	@Override
	public void codecRemoved(SignalMLCodecManagerEvent ev) {
		try {
			SvarogCodecEvent event = createCodecEvent(ev);
			for (SvarogCodecListener listener : codecListeners){
				try {
					listener.codecRemoved(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on codec removed");
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface when codec was removed");
			e.printStackTrace();
		}
	}

	/**
	 * Does nothing.
	 * @param ev an event associated with a change
	 */
	@Override
	public void codecsChanged(SignalMLCodecManagerEvent ev) {
		//nothing to do
	}

	/**
	 * Registers a focus listener for a given signal view,
	 * if the view is of type signal view.
	 * If the listener is already registered does nothing.
	 * @param view the view
	 */
	private void registerFocusListener(DocumentView view){
		if (view != null && view instanceof SignalView){
			SignalView signalView = (SignalView) view;
			signalView.removeActionFocusListener(this);
			signalView.addActionFocusListener(this);
			PositionedTag positionedTag = signalView.getActiveTag();
			if (positionedTag != null){
				activeTags.put(signalView, positionedTag.getTag());
			} else {
				activeTags.put(signalView, null);
			}
			
		}
	}
	
	/**
	 * Informs listeners that the {@link DocumentView} changed and
	 * registers focus listeners for the new view.
	 * @param evt an event describing this change
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			if (evt.getPropertyName().equals(Document.DOCUMENT_VIEW_PROPERTY)){
				if  (!(evt.getSource() instanceof Document)) return;
				Document document = (Document) evt.getSource();
				if ((evt.getOldValue() == null || evt.getOldValue() instanceof DocumentView) &&
						(evt.getOldValue() == null ||evt.getNewValue() instanceof DocumentView)){
					DocumentView oldView = (DocumentView) evt.getOldValue();
					DocumentView newView = (DocumentView) evt.getNewValue();
					registerFocusListener(newView);
					DocumentViewEventImpl event = new DocumentViewEventImpl(document, oldView);
					for (SvarogDocumentListener listener : documentListeners){
						try {
							listener.documentViewChanged(event);
						} catch (Exception ex) {
							logger.error("unhandled exception in plugin on document view changed");
							ex.printStackTrace();
						}
					}
				}
				
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface when property has changed");
			e.printStackTrace();
		}
	}
	
	@Override
	public void tagAdded(TagEvent e) {
		try {
			SvarogTagEvent event = createTagEvent(e);
			for (SvarogTagListener listener: tagListenersWithActive){
				singleTagAdded(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was added");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void tagRemoved(TagEvent e) {
		try {
			SvarogTagEvent event = createTagEvent(e);
			for (SvarogTagListener listener: tagListenersWithActive){
				singleTagRemoved(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was removed");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void tagChanged(TagEvent e) {
		try {
			SvarogTagEvent event = createTagEvent(e);
			for (SvarogTagListener listener: tagListenersWithActive){
				singleTagChanged(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag has changed");
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
		ActionFocusManager focusManager = manager.getActionFocusManager();
		if (focusManager != null) {
			focusManager.addActionFocusListener(this);
			activeDocument = focusManager.getActiveDocument();
			activeTagDocument = focusManager.getActiveTagDocument();
		}
		
		DocumentManager documentManager = manager.getDocumentManager();
		if (documentManager != null) documentManager.addDocumentManagerListener(this);
		
		SignalMLCodecManager codecManager = manager.getCodecManager();
		if (codecManager != null) codecManager.addSignalMLCodecManagerListener(this);
		for (ChangeSupportDocumentImpl listener : listenersOnSignalDocument.values()){
			listener.setManager(manager);
		}
		for (ChangeSupportDocumentImpl listener : listenersOnTagDocument.values()){
			listener.setManager(manager);
		}
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addCloseListener(org.signalml.plugin.export.change.SvarogCloseListener)
	 */
	@Override
	public void addCloseListener(SvarogCloseListener closeListener) {
		closeListeners.add(closeListener);
		
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addDocumentListener(org.signalml.plugin.export.change.SvarogDocumentListener)
	 */
	@Override
	public void addDocumentListener(SvarogDocumentListener documentListener) {
		documentListeners.add(documentListener);
		
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagDocumentListener(org.signalml.plugin.export.change.SvarogTagDocumentListener)
	 */
	@Override
	public void addTagDocumentListener(SvarogTagDocumentListener tagDocumentListener) {
		tagDocumentListeners.add(tagDocumentListener);
		
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagListener(org.signalml.plugin.export.change.SvarogTagListenerWithAcitve)
	 */
	@Override
	public void addTagListener(SvarogTagListenerWithAcitve tagListener) {
		tagListenersWithActive.add(tagListener);
		
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagListenerForTagDocument(org.signalml.plugin.export.change.SvarogTagListener, org.signalml.plugin.export.signal.ExportedTagDocument)
	 */
	@Override
	public void addTagListenerForTagDocument(SvarogTagListener tagListener,	ExportedTagDocument document) {
		ChangeSupportDocumentImpl listener = listenersOnTagDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such tag document");
		listener.addTagListener(tagListener);
		
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagListenerForSignalDocument(org.signalml.plugin.export.change.SvarogTagListener, org.signalml.plugin.export.signal.ExportedSignalDocument)
	 */
	@Override
	public void addTagListenerForSignalDocument(SvarogTagListener tagListener, ExportedSignalDocument document) {
		ChangeSupportDocumentImpl listener = listenersOnSignalDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such signal document");
		listener.addTagListener(tagListener);
		
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagStyleListenerForTagDocument(org.signalml.plugin.export.change.SvarogTagStyleListener, org.signalml.plugin.export.signal.ExportedTagDocument)
	 */
	@Override
	public void addTagStyleListenerForTagDocument(SvarogTagStyleListener tagStyleListener, ExportedTagDocument document) {
		ChangeSupportDocumentImpl listener = listenersOnTagDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such tag document");
		listener.addTagStyleListener(tagStyleListener);
		
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagStyleListenerForSignalDocument(org.signalml.plugin.export.change.SvarogTagStyleListener, org.signalml.plugin.export.signal.ExportedSignalDocument)
	 */
	@Override
	public void addTagStyleListenerForSignalDocument(SvarogTagStyleListener tagStyleListener, ExportedSignalDocument document) {
		ChangeSupportDocumentImpl listener = listenersOnSignalDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such signal document");
		listener.addTagStyleListener(tagStyleListener);
		
	}
	
	/**
	 * Informs listeners that application is closing
	 */
	public void onClose(){
		for (SvarogCloseListener listener : closeListeners)
			listener.applicationClosing();
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addCodecListener(org.signalml.plugin.export.change.SvarogCodecListener)
	 */
	@Override
	public void addCodecListener(SvarogCodecListener codecListener) {
		codecListeners.add(codecListener);
		
	}
}
