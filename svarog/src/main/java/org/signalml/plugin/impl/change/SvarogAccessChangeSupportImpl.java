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
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecManagerEvent;
import org.signalml.codec.SignalMLCodecManagerListener;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagEvent;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.events.PluginCodecEvent;
import org.signalml.plugin.export.change.events.PluginDocumentEvent;
import org.signalml.plugin.export.change.events.PluginSignalChangeEvent;
import org.signalml.plugin.export.change.events.PluginTagEvent;
import org.signalml.plugin.export.change.listeners.PluginCloseListener;
import org.signalml.plugin.export.change.listeners.PluginCodecListener;
import org.signalml.plugin.export.change.listeners.PluginDocumentListener;
import org.signalml.plugin.export.change.listeners.PluginSignalChangeListener;
import org.signalml.plugin.export.change.listeners.PluginTagDocumentListener;
import org.signalml.plugin.export.change.listeners.PluginTagListener;
import org.signalml.plugin.export.change.listeners.PluginTagListenerWithActive;
import org.signalml.plugin.export.change.listeners.PluginTagStyleListener;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.impl.change.events.PluginActiveDocumentEventImpl;
import org.signalml.plugin.impl.change.events.PluginActiveTagEventImpl;
import org.signalml.plugin.impl.change.events.PluginCodecEventImpl;
import org.signalml.plugin.impl.change.events.PluginDocumentEventImpl;
import org.signalml.plugin.impl.change.events.PluginDocumentViewEventImpl;
import org.signalml.plugin.impl.change.events.PluginTagDocumentEventImpl;


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
 * {@link SvarogAccessChangeSupportDocumentImpl document change support}.
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
 * {@link SvarogAccessChangeSupportDocumentImpl document change support} and registers
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
public class SvarogAccessChangeSupportImpl extends SvarogAccessChangeSupportDocumentImpl implements SvarogAccessChangeSupport, ActionFocusListener, DocumentManagerListener, SignalMLCodecManagerListener, PropertyChangeListener, PluginSignalChangeListener {

	/**
	 * the currently active document
	 */
	private Document activeDocument = null;
	/**
	 * the currently active tag document
	 */
	private TagDocument activeTagDocument = null;

	/**
	 * {@link PluginCloseListener listeners} on close of Svarog
	 */
	private ArrayList<PluginCloseListener> closeListeners = new ArrayList<>();

	/**
	 * {@link PluginCodecListener listeners} on codec changes (addition
	 * and removal)
	 */
	private ArrayList<PluginCodecListener> codecListeners = new ArrayList<>();

	/**
	 * {@link PluginDocumentListener listeners} on changes associated
	 * with a {@link Document}
	 */
	private ArrayList<PluginDocumentListener> documentListeners = new ArrayList<>();

	/**
	 * {@link PluginTagDocumentListener listeners} on changes of
	 * an active {@link TagDocument}
	 */
	private ArrayList<PluginTagDocumentListener> tagDocumentListeners = new ArrayList<>();

	/**
	 * {@link PluginTagListenerWithActive listeners} on {@link ExportedTag tag}
	 * changes (addition, removal, change) including changes of an active tag
	 */
	protected ArrayList<PluginTagListenerWithActive> tagListenersWithActive = new ArrayList<>();

	/**
	 * {@link PluginSignalChangeListener listeners} on signal changes.
	 */
	protected ArrayList<PluginSignalChangeListener> signalListeners = new ArrayList<>();

	/**
	 * HashMap associating signal documents with {@link SvarogAccessChangeSupportDocumentImpl listeners} for them
	 */
	private HashMap<ExportedSignalDocument, SvarogAccessChangeSupportDocumentImpl> listenersOnSignalDocument = new HashMap<>();

	/**
	 * HashMap associating tag documents with {@link SvarogAccessChangeSupportDocumentImpl listeners} for them
	 */
	private HashMap<ExportedTagDocument, SvarogAccessChangeSupportDocumentImpl> listenersOnTagDocument = new HashMap<>();



	/**
	 * HashMap associating signal views with tags active in them
	 */
	private HashMap<SignalView, Tag> activeTags = new HashMap<>();

	private SvarogAccessChangeSupportImpl() {
	}

	private static final SvarogAccessChangeSupportImpl _instance = new SvarogAccessChangeSupportImpl();

	public static SvarogAccessChangeSupportImpl getInstance() {
		return _instance;
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
		try {
			ActionFocusManager focusManager = e.getActionFocusManager();
			if (focusManager != null) {
				Document currentActiveDocument = focusManager.getActiveDocument();
				if (currentActiveDocument != activeDocument) {
					PluginActiveDocumentEventImpl event = new PluginActiveDocumentEventImpl(currentActiveDocument, activeDocument);
					activeDocument = currentActiveDocument;
					for (PluginDocumentListener listener : documentListeners) {
						try {
							listener.activeDocumentChanged(event);
						} catch (Exception ex) {
							logger.error("unhandled exception in plugin on active document change", ex);
						}
					}
				} else {
					TagDocument currentActiveTagDocument = focusManager.getActiveTagDocument();
					if (currentActiveTagDocument != activeTagDocument) {
						PluginTagDocumentEventImpl event = new PluginTagDocumentEventImpl(currentActiveTagDocument, activeTagDocument);
						activeTagDocument = currentActiveTagDocument;
						for (PluginTagDocumentListener listener : tagDocumentListeners) {
							try {
								listener.activeTagDocumentChanged(event);
							} catch (Exception ex) {
								logger.error("unhandled exception in plugin on active tag document change", ex);
							}
						}
					}
				}

			} else {
				if (e.getSource() instanceof SignalView) {
					SignalView signalView = (SignalView) e.getSource();
					PositionedTag positionedTag = signalView.getActiveTag();
					Tag newActiveTag;
					if (positionedTag != null)
						newActiveTag = positionedTag.getTag();
					else newActiveTag = null;
					ExportedTag oldActiveTag = activeTags.get(signalView);
					if (oldActiveTag != newActiveTag) {
						PluginActiveTagEventImpl event = new PluginActiveTagEventImpl(newActiveTag, oldActiveTag);
						for (PluginTagListenerWithActive listener : tagListenersWithActive) {
							try {
								listener.activeTagChanged(event);
							} catch (Exception ex) {
								logger.error("unhandled exception in plugin on active tag change", ex);
							}
						}
						activeTags.put(signalView, newActiveTag);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when action focus changed", ex);
		}
	}

	/**
	 * Adds provided listener and the this listener to the {@link StyledTagSet set}
	 * associated with a given {@link TagDocument}.
	 * @param tagDocument the tag document
	 * @param listener the listener to be added
	 */
	private void addListenersForTagDocument(TagDocument tagDocument, SvarogAccessChangeSupportDocumentImpl listener) {
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
		SvarogAccessChangeSupportDocumentImpl parentListener = listenersOnSignalDocument.get(parent);
		if (parentListener != null) {
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
	 * {@link #addListenersForTagDocument(TagDocument, SvarogAccessChangeSupportDocumentImpl)} once,
	 * if document is of type {@link SignalDocument} adds listeners for all
	 * dependent tag documents.
	 * @param e an event associated with a change
	 */
	private void addListenersForDocument(DocumentManagerEvent e) {
		Document document = e.getDocument();
		if (document ==  null) return;

		document.addPropertyChangeListener(this);

		DocumentView documentView = document.getDocumentView();
		if (documentView != null) {
			if (documentView instanceof SignalView) {
				registerFocusListener(documentView);
			}
		}


		if (document instanceof TagDocument || document instanceof SignalDocument) {
			SvarogAccessChangeSupportDocumentImpl tagDocumentListener = new SvarogAccessChangeSupportDocumentImpl();
			tagDocumentListener.setViewerElementManager(getViewerElementManager());

			if (document instanceof TagDocument) {
				TagDocument tagDocument = (TagDocument) document;
				addListenersForTagDocument(tagDocument, tagDocumentListener);
			} else {
				SignalDocument signalDocument = (SignalDocument) document;
				if (listenersOnSignalDocument.get(signalDocument) == null) {
					listenersOnSignalDocument.put(signalDocument, tagDocumentListener);
					List<TagDocument> tagDocuments = signalDocument.getTagDocuments();
					for (TagDocument tagDocument : tagDocuments) {
						StyledTagSet tagSet = tagDocument.getTagSet();
						tagSet.removeTagListener(this);
						tagSet.removeTagStyleListener(this);

						tagSet.addTagListener(this);
						tagSet.addTagListener(tagDocumentListener);
						tagSet.addTagStyleListener(this);
						tagSet.addTagStyleListener(tagDocumentListener);
					}
				}
				signalDocument.getSampleSource().addSignalChangeListener(this);
			}

		}
	}

	/**
	 * Creates a {@link PluginDocumentEvent} from given
	 * {@link DocumentManagerEvent}.
	 * Sets the document.
	 * @param e the DocumentManagerEvent to be used
	 * @return created SvarogDocumentEvent
	 */
	private PluginDocumentEvent createDocumentEvent(DocumentManagerEvent e) {
		Document document = e.getDocument();
		PluginDocumentEventImpl event = new PluginDocumentEventImpl(document);
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
			PluginDocumentEvent event = createDocumentEvent(e);
			for (PluginDocumentListener listener : documentListeners) {
				try {
					listener.documentAdded(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on document added", ex);
				}

			}
		} catch (Exception e2) {
			logger.error("Unknown error in plug-in interface when document was added", e2);
		}
	}

	/**
	 * Informs listeners that the document was removed.
	 * @param e an event associated with a change
	 */
	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		try {
			PluginDocumentEvent event = createDocumentEvent(e);
			for (PluginDocumentListener listener : documentListeners) {
				try {
					listener.documentRemoved(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on document removed", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when document was removed", ex);
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
	 * Creates a {@link PluginCodecEvent} from given
	 * {@link SignalMLCodecManagerEvent}.
	 * Sets the format name of the codec.
	 * @param ev the SignalMLCodecManagerEvent to be used
	 * @return created SvarogCodecEvent
	 */
	private PluginCodecEvent createCodecEvent(SignalMLCodecManagerEvent ev) {
		SignalMLCodec codec = ev.getCodec();
		String formatName = null;
		if (codec == null) throw new RuntimeException("no codec in the SignalMLCodecManagerEvent");
		formatName = codec.getFormatName();
		PluginCodecEventImpl event = new PluginCodecEventImpl(formatName);
		return event;
	}


	/**
	 * Informs listeners that a codec was added.
	 * @param ev an event describing this change
	 */
	@Override
	public void codecAdded(SignalMLCodecManagerEvent ev) {
		try {
			PluginCodecEvent event = createCodecEvent(ev);
			for (PluginCodecListener listener : codecListeners) {
				try {
					listener.codecAdded(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on codec added", ex);
				}
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface when codec was added", e);
		}
	}

	/**
	 * Informs listeners that a codec was removed.
	 * @param ev an event describing this change
	 */
	@Override
	public void codecRemoved(SignalMLCodecManagerEvent ev) {
		try {
			PluginCodecEvent event = createCodecEvent(ev);
			for (PluginCodecListener listener : codecListeners) {
				try {
					listener.codecRemoved(event);
				} catch (Exception ex) {
					logger.error("unhandled exception in plugin on codec removed", ex);
				}
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface when codec was removed", e);
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
	private void registerFocusListener(DocumentView view) {
		if (view != null && view instanceof SignalView) {
			SignalView signalView = (SignalView) view;
			signalView.removeActionFocusListener(this);
			signalView.addActionFocusListener(this);
			PositionedTag positionedTag = signalView.getActiveTag();
			if (positionedTag != null) {
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
			if (evt.getPropertyName().equals(Document.DOCUMENT_VIEW_PROPERTY)) {
				if (!(evt.getSource() instanceof Document)) return;
				Document document = (Document) evt.getSource();
				if ((evt.getOldValue() == null || evt.getOldValue() instanceof DocumentView) &&
						(evt.getOldValue() == null ||evt.getNewValue() instanceof DocumentView)) {
					DocumentView oldView = (DocumentView) evt.getOldValue();
					DocumentView newView = (DocumentView) evt.getNewValue();
					registerFocusListener(newView);
					PluginDocumentViewEventImpl event = new PluginDocumentViewEventImpl(document, oldView);
					for (PluginDocumentListener listener : documentListeners) {
						try {
							listener.documentViewChanged(event);
						} catch (Exception ex) {
							logger.error("unhandled exception in plugin on document view changed", ex);
						}
					}
				}

			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface when property has changed", e);
		}
	}

	@Override
	public void tagAdded(TagEvent e) {
		try {
			PluginTagEvent event = createTagEvent(e);
			for (PluginTagListener listener: tagListenersWithActive) {
				singleTagAdded(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was added", ex);
		}
	}

	@Override
	public void tagRemoved(TagEvent e) {
		try {
			PluginTagEvent event = createTagEvent(e);
			for (PluginTagListener listener: tagListenersWithActive) {
				singleTagRemoved(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag was removed", ex);
		}
	}

	@Override
	public void tagChanged(TagEvent e) {
		try {
			PluginTagEvent event = createTagEvent(e);
			for (PluginTagListener listener: tagListenersWithActive) {
				singleTagChanged(event, listener);
			}
		} catch (Exception ex) {
			logger.error("Unknown error in plug-in interface when tag has changed", ex);
		}
	}


	/**
	 * Sets the element manager, stores active documents and
	 * adds listeners for codec manager and document manager.
	 * @param elementManager the element manager to set
	 */
	public void setManager(ViewerElementManager elementManager) {
		super.setViewerElementManager(elementManager);
		ViewerElementManager manager = getViewerElementManager();
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
		for (SvarogAccessChangeSupportDocumentImpl listener : listenersOnSignalDocument.values()) {
			listener.setViewerElementManager(manager);
		}
		for (SvarogAccessChangeSupportDocumentImpl listener : listenersOnTagDocument.values()) {
			listener.setViewerElementManager(manager);
		}
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addCloseListener(org.signalml.plugin.export.change.SvarogCloseListener)
	 */
	@Override
	public void addCloseListener(PluginCloseListener closeListener) {
		closeListeners.add(closeListener);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addDocumentListener(org.signalml.plugin.export.change.SvarogDocumentListener)
	 */
	@Override
	public void addDocumentListener(PluginDocumentListener documentListener) {
		documentListeners.add(documentListener);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagDocumentListener(org.signalml.plugin.export.change.SvarogTagDocumentListener)
	 */
	@Override
	public void addTagDocumentListener(PluginTagDocumentListener tagDocumentListener) {
		tagDocumentListeners.add(tagDocumentListener);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagListener(org.signalml.plugin.export.change.SvarogTagListenerWithAcitve)
	 */
	@Override
	public void addTagListener(PluginTagListenerWithActive tagListener) {
		tagListenersWithActive.add(tagListener);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagListenerForTagDocument(org.signalml.plugin.export.change.SvarogTagListener, org.signalml.plugin.export.signal.ExportedTagDocument)
	 */
	@Override
	public void addTagListenerForTagDocument(PluginTagListener tagListener,	ExportedTagDocument document) {
		SvarogAccessChangeSupportDocumentImpl listener = listenersOnTagDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such tag document");
		listener.addTagListener(tagListener);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagListenerForSignalDocument(org.signalml.plugin.export.change.SvarogTagListener, org.signalml.plugin.export.signal.ExportedSignalDocument)
	 */
	@Override
	public void addTagListenerForSignalDocument(PluginTagListener tagListener, ExportedSignalDocument document) {
		SvarogAccessChangeSupportDocumentImpl listener = listenersOnSignalDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such signal document");
		listener.addTagListener(tagListener);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagStyleListenerForTagDocument(org.signalml.plugin.export.change.SvarogTagStyleListener, org.signalml.plugin.export.signal.ExportedTagDocument)
	 */
	@Override
	public void addTagStyleListenerForTagDocument(PluginTagStyleListener tagStyleListener, ExportedTagDocument document) {
		SvarogAccessChangeSupportDocumentImpl listener = listenersOnTagDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such tag document");
		listener.addTagStyleListener(tagStyleListener);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addTagStyleListenerForSignalDocument(org.signalml.plugin.export.change.SvarogTagStyleListener, org.signalml.plugin.export.signal.ExportedSignalDocument)
	 */
	@Override
	public void addTagStyleListenerForSignalDocument(PluginTagStyleListener tagStyleListener, ExportedSignalDocument document) {
		SvarogAccessChangeSupportDocumentImpl listener = listenersOnSignalDocument.get(document);
		if (listener == null) throw new IllegalArgumentException("no such signal document");
		listener.addTagStyleListener(tagStyleListener);

	}

	/**
	 * Informs listeners that application is closing
	 */
	public void onClose() {
		for (PluginCloseListener listener : closeListeners)
			try {
				listener.applicationClosing();
			} catch (Exception e) {
				logger.error("Unhandled exception in plugin on application closing");
				logger.error("", e);
			}
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.SvarogAccessChangeSupport#addCodecListener(org.signalml.plugin.export.change.SvarogCodecListener)
	 */
	@Override
	public void addCodecListener(PluginCodecListener codecListener) {
		codecListeners.add(codecListener);

	}

	public void setViewerElementManager(ViewerElementManager viewerElementManager) {

		super.setViewerElementManager(viewerElementManager);
		ViewerElementManager manager = getViewerElementManager();
		ActionFocusManager focusManager = manager.getActionFocusManager();
		if (focusManager != null) {
			focusManager.addActionFocusListener(this);
			activeDocument = focusManager.getActiveDocument();
			activeTagDocument = focusManager.getActiveTagDocument();
		}

		DocumentManager documentManager = manager.getDocumentManager();
		if (documentManager != null)
			documentManager.addDocumentManagerListener(this);

		SignalMLCodecManager codecManager = manager.getCodecManager();
		if (codecManager != null)
			codecManager.addSignalMLCodecManagerListener(this);
		for (SvarogAccessChangeSupportDocumentImpl listener : listenersOnSignalDocument.values()) {
			listener.setViewerElementManager(manager);
		}
		for (SvarogAccessChangeSupportDocumentImpl listener : listenersOnTagDocument.values()) {
			listener.setViewerElementManager(manager);
		}

	}

	@Override
	public void addSignalChangeListener(PluginSignalChangeListener signalListener) {
		signalListeners.add(signalListener);
	}

	@Override
	public void newSamplesAdded(PluginSignalChangeEvent e) {
		if (e.getDocument() == activeDocument)
			for (PluginSignalChangeListener listener : signalListeners)
				listener.newSamplesAdded(e);
	}
}
