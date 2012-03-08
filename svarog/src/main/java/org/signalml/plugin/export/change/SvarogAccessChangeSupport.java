/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.app.document.TagDocument;

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
import org.signalml.plugin.export.signal.ExportedTagStyle;


/**
 * This interface is used by plug-ins to listen on changes in Svarog.
 * Allows to add listeners:
 * <ul>
 * <li>{@link PluginCloseListener}</li>
 * <li>{@link PluginCodecListener}</li>
 * <li>{@link PluginDocumentListener}</li>
 * <li>{@link PluginTagDocumentListener}</li>
 * <li>{@link PluginTagListenerWithActive}</li>
 * <li>{@link PluginTagStyleListener}</li>
 * </ul>
 * to listen on specified changes in whole Svarog.
 * <p>
 * It is also possible to add listeners:
 * <ul>
 * <li>{@link PluginTagStyleListener}</li>
 * <li>{@link PluginTagListener}</li>
 * to listen on changes concerning only one {@link ExportedTagDocument tag}
 * or {@link ExportedSignalDocument signal} document.
 * 
 * @author Marcin Szumski
 */
public interface SvarogAccessChangeSupport {
	
	/**
	 * Adds a {@link PluginCloseListener listener} on close of Svarog.
	 * @param closeListener the listener to add
	 */
	void addCloseListener(PluginCloseListener closeListener);
	
	/**
	 * Adds a {@link PluginCodecListener listener} on codec changes
	 * (addition and removal).
	 * @param codecListener the listener to add
	 */
	void addCodecListener(PluginCodecListener codecListener);
	
	/**
	 * Adds a {@link PluginDocumentListener listener} on changes associated
	 * with a {@link Document}.
	 * @param documentListener the listener to add
	 */
	void addDocumentListener(PluginDocumentListener documentListener);
	
	/**
	 * Adds a {@link PluginTagDocumentListener listener} on changes of
	 * an active {@link TagDocument}.
	 * @param tagDocumentListener the listener to add
	 */
	void addTagDocumentListener(PluginTagDocumentListener tagDocumentListener);
	
	/**
	 * Adds a {@link PluginTagListenerWithActive listener} on
	 * {@link ExportedTag tag} changes (addition, removal, change)
	 * including changes of an active tag
	 * @param tagListener the listener to add
	 */
	void addTagListener(PluginTagListenerWithActive tagListener);
	
	/**
	 * Adds a {@link PluginTagStyleListener listener} on
	 * {@link ExportedTagStyle tag style} changes (addition, removal, change).
	 * @param tagStyleListener the listener to add
	 */
	void addTagStyleListener(PluginTagStyleListener tagStyleListener);
	
	/**
	 * Adds a {@link PluginTagListener listener} on {@link ExportedTag tag}
	 * changes (addition, removal, change) for a specified
	 * {@link ExportedTagDocument document with tags}.
	 * @param tagListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagListenerForTagDocument(PluginTagListener tagListener, ExportedTagDocument document);
	
	/**
	 * Adds a {@link PluginTagListener listener} on {@link ExportedTag tag}
	 * changes (addition, removal, change) for all {@link ExportedTagDocument
	 * tag documents} associated with a specified {@link ExportedSignalDocument
	 * signal document}.
	 * @param tagListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagListenerForSignalDocument(PluginTagListener tagListener, ExportedSignalDocument document);
	
	/**
	 * Adds a {@link PluginTagStyleListener listener} on
	 * {@link ExportedTagStyle tag style} changes (addition, removal, change)
	 * for a specified {@link ExportedTagDocument document with tags}.
	 * @param tagStyleListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagStyleListenerForTagDocument(PluginTagStyleListener tagStyleListener, ExportedTagDocument document);
	
	/**
	 * Adds a {@link PluginTagStyleListener listener} on
	 * {@link ExportedTagStyle tag style} changes (addition, removal, change)
	 * for all {@link ExportedTagDocument tag documents} associated with
	 * a specified {@link ExportedSignalDocument signal document}.
	 * @param tagStyleListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagStyleListenerForSignalDocument(PluginTagStyleListener tagStyleListener, ExportedSignalDocument document);

	/**
	 * Adds a {@link PluginSignalChangeListener listener} on
	 * signal changes.
	 * @param signalListener
	 */
	void addSignalChangeListener(PluginSignalChangeListener signalListener);

}
