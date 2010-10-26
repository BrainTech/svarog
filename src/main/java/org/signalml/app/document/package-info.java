
/**
 * This package contains the class hierarchy of
 * {@link org.signalml.plugin.export.signal.Document documents}.
 * The most important {@link org.signalml.app.document.ManagedDocumentType
 * types} of documents used in this hierarchy, are:
 * <ul>
 * <li> {@link org.signalml.app.document.SignalDocument SignalDocument}
 * <ul>
 * <li>{@link org.signalml.app.document.SignalMLDocument SignalMLDocument}</li>
 * <li>{@link org.signalml.app.document.RawSignalDocument RawSignalDocument}</li>
 * <li> {@link org.signalml.app.document.MonitorSignalDocument
 * MonitorSignalDocument}</li></ul></li>
 * <li>{@link org.signalml.app.document.TagDocument TagDocument}</li>
 * <li>{@link org.signalml.app.document.BookDocument BookDocument}</li>
 * </ul>
 * <p>
 * The second part of this package is the
 * {@link org.signalml.app.document.DocumentFlowIntegrator
 * DocumentFlowIntegrator}, which is responsible for opening, closing and
 * saving the documents.
 * <p>
 * Opened documents are stored in the
 * {@link org.signalml.app.document.DocumentManager DocumentManager} and the
 * {@link org.signalml.app.document.MRUDEntry descriptions} of last open
 * files in the {@link org.signalml.app.document.MRUDRegistry MRUDRegistry}.
 */
package org.signalml.app.document;