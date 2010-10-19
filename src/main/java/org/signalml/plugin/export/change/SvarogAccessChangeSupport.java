/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.app.document.TagDocument;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;


/**
 * This interface is used by plug-ins to listen on changes in Svarog.
 * Allows to add listeners:
 * <ul>
 * <li>{@link SvarogCloseListener}</li>
 * <li>{@link SvarogCodecListener}</li>
 * <li>{@link SvarogDocumentListener}</li>
 * <li>{@link SvarogTagDocumentListener}</li>
 * <li>{@link SvarogTagListenerWithAcitve}</li>
 * <li>{@link SvarogTagStyleListener}</li>
 * </ul>
 * to listen on specified changes in whole Svarog.
 * <p>
 * It is also possible to add listeners:
 * <ul>
 * <li>{@link SvarogTagStyleListener}</li>
 * <li>{@link SvarogTagListener}</li>
 * to listen on changes concerning only one {@link ExportedTagDocument tag}
 * or {@link ExportedSignalDocument signal} document.
 * 
 * @author Marcin Szumski
 */
public interface SvarogAccessChangeSupport {
	
	/**
	 * Adds a {@link SvarogCloseListener listener} on close of Svarog.
	 * @param closeListener the listener to add
	 */
	void addCloseListener(SvarogCloseListener closeListener);
	
	/**
	 * Adds a {@link SvarogCodecListener listener} on codec changes
	 * (addition and removal).
	 * @param codecListener the listener to add
	 */
	void addCodecListener(SvarogCodecListener codecListener);
	
	/**
	 * Adds a {@link SvarogDocumentListener listener} on changes associated
	 * with a {@link Document}.
	 * @param documentListener the listener to add
	 */
	void addDocumentListener(SvarogDocumentListener documentListener);
	
	/**
	 * Adds a {@link SvarogTagDocumentListener listener} on changes of
	 * an active {@link TagDocument}.
	 * @param tagDocumentListener the listener to add
	 */
	void addTagDocumentListener(SvarogTagDocumentListener tagDocumentListener);
	
	/**
	 * Adds a {@link SvarogTagListenerWithAcitve listener} on
	 * {@link ExportedTag tag} changes (addition, removal, change)
	 * including changes of an active tag
	 * @param tagListener the listener to add
	 */
	void addTagListener(SvarogTagListenerWithAcitve tagListener);
	
	/**
	 * Adds a {@link SvarogTagStyleListener listener} on
	 * {@link ExportedTagStyle tag style} changes (addition, removal, change).
	 * @param tagStyleListener the listener to add
	 */
	void addTagStyleListener(SvarogTagStyleListener tagStyleListener);
	
	/**
	 * Adds a {@link SvarogTagListener listener} on {@link ExportedTag tag}
	 * changes (addition, removal, change) for a specified
	 * {@link ExportedTagDocument document with tags}.
	 * @param tagListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagListenerForTagDocument(SvarogTagListener tagListener, ExportedTagDocument document);
	
	/**
	 * Adds a {@link SvarogTagListener listener} on {@link ExportedTag tag}
	 * changes (addition, removal, change) for all {@link ExportedTagDocument
	 * tag documents} associated with a specified {@link ExportedSignalDocument
	 * signal document}.
	 * @param tagListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagListenerForSignalDocument(SvarogTagListener tagListener, ExportedSignalDocument document);
	
	/**
	 * Adds a {@link SvarogTagStyleListener listener} on
	 * {@link ExportedTagStyle tag style} changes (addition, removal, change)
	 * for a specified {@link ExportedTagDocument document with tags}.
	 * @param tagStyleListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagStyleListenerForTagDocument(SvarogTagStyleListener tagStyleListener, ExportedTagDocument document);
	
	/**
	 * Adds a {@link SvarogTagStyleListener listener} on
	 * {@link ExportedTagStyle tag style} changes (addition, removal, change)
	 * for all {@link ExportedTagDocument tag documents} associated with
	 * a specified {@link ExportedSignalDocument signal document}.
	 * @param tagStyleListener the listener to add
	 * @param document the document for which the listener is added
	 */
	void addTagStyleListenerForSignalDocument(SvarogTagStyleListener tagStyleListener, ExportedSignalDocument document);
}
