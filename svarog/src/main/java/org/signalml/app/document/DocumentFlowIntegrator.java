/* DocumentFlowIntegrator.java created 2007-09-19
 *
 */

package org.signalml.app.document;

import static javax.swing.JOptionPane.showOptionDialog;
import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.mrud.MRUDEntry;
import org.signalml.app.document.mrud.MRUDRegistry;
import org.signalml.app.document.signal.AbstractFileSignal;
import org.signalml.app.document.signal.BaseSignalDocument;
import org.signalml.app.document.signal.RawSignalDocument;
import org.signalml.app.document.signal.RawSignalMRUDEntry;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.document.signal.SignalMLDocument;
import org.signalml.app.document.signal.SignalMLMRUDEntry;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.OpenTagDescriptor;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.model.montage.MontagePresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.video.OfflineVideoFrame;
import org.signalml.app.video.VideoFrame;
import org.signalml.app.view.book.BookView;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.signal.SignalParametersDialog;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.app.worker.document.OpenBookDocumentWorker;
import org.signalml.app.worker.document.OpenSignalMLDocumentWorker;
import org.signalml.app.worker.document.OpenTagDocumentWorker;
import org.signalml.app.worker.document.SaveDocumentWorker;
import org.signalml.app.worker.signal.SignalChecksumWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.app.document.signal.AsciiSignalDocument;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagSignalIdentification;
import org.signalml.exception.MissingCodecException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.util.Util;

/**
 * Integrates the flow of {@link Document documents}.
 * Allows to:
 * <ul>
 * <li>open the document or all documents based on a
 * {@link OpenDocumentDescriptor descriptor},</li>
 * <li>open the document based on a {@link MRUDEntry},</li>
 * <li>close the document or all documents,</li>
 * <li>save the document or all documents,</li>
 * <li>check if all documents are saved,</li>
 * </ul>
 * for the following types of documents:
 * <ul>
 * <li> {@link SignalDocument}</li>
 * <li> {@link MonitorSignalDocument}</li>
 * <li> {@link TagDocument}</li>
 * <li> {@link BookDocument}</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DocumentFlowIntegrator {

	protected static final Logger logger = Logger.getLogger(DocumentFlowIntegrator.class);

	/**
	 * the {@link DocumentManager manager} of {@link Document documents} in Svarog
	 */
	private DocumentManager documentManager;

	/**
	 * the {@link MRUDRegistry cache} of {@link MRUDEntry file descriptions}
	 */
	private MRUDRegistry mrudRegistry;

	/**
	 * the manager of {@link SignalMLCodec codecs}
	 */
	private SignalMLCodecManager codecManager;

	/**
	 * the parent pane to all dialogs shown by this integrator
	 */
	private Component optionPaneParent = null;

	/**
	 * the {@link ViewerFileChooser chooser} for files in Svarog
	 */
	private ViewerFileChooser fileChooser;

	/**
	 * the {@link SignalParametersDialog dialog} to get parameters of the
	 * signal
	 */
	private SignalParametersDialog signalParametersDialog;

	/**
	 * the {@link ActionFocusManager manager} of active elements in Svarog
	 */
	private ActionFocusManager actionFocusManager;

	/**
	 * the {@link ApplicationConfiguration configuration} of Svarog
	 */
	private ApplicationConfiguration applicationConfig;

	/**
	 * the {@link MontagePresetManager manager} of montage presets
	 */
	private MontagePresetManager montagePresetManager;

	/**
	 * the {@link PleaseWaitDialog dialog} that tells the user to wait
	 */
	private PleaseWaitDialog pleaseWaitDialog;

	/**
	 * Opens a {@link Document document} described in a given
	 * {@link OpenDocumentDescriptor descriptor} and returns it.
	 * Depending on a {@link ManagedDocumentType type} of a document
	 * {@link OpenDocumentDescriptor#getType() obtained} from the descriptor
	 * calls the function that opens that type.
	 * @param descriptor the descriptor of a document to open
	 * @return the opened document
	 * @throws IOException if the file doesn't exist or is unreadable or I/O
	 * error occurs while opening RAW signal
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or if {@link SignalChecksumWorker checksum
	 * worker} was interrupted or failed to calculate the checksum
	 */
	public Document openDocument(OpenDocumentDescriptor descriptor) throws IOException, SignalMLException {
		ManagedDocumentType type = descriptor.getType();
		if (type.equals(ManagedDocumentType.SIGNAL)) {
			return openSignalDocument(descriptor);
		}
		if (type.equals(ManagedDocumentType.MONITOR)) {
			return openMonitorDocument(descriptor);
		} else if (type.equals(ManagedDocumentType.BOOK)) {
			return openBookDocument(descriptor);
		} else if (type.equals(ManagedDocumentType.TAG)) {
			return openTagDocument(descriptor);
		} else {
			logger.error("Unsupported type [" + type + "]");
			throw new ClassCastException();
		}
	}

	/**
	 * Tries to {@link #openDocument(OpenDocumentDescriptor) open} a
	 * {@link Document document}. If this operation fails
	 * a dialog with the description of an error is shown.
	 * @param descriptor the descriptor of a document to open
	 * @param window the window that should be parent to errors dialog
	 * @return true if the operation is successful, false otherwise
	 */
	public Document maybeOpenDocument(OpenDocumentDescriptor descriptor, Window window) {
		try {
			return this.openDocument(descriptor);
		} catch (SignalMLException ex) {
			logger.error("Failed to open document", ex);
			Dialogs.showExceptionDialog(window, ex);
		} catch (IOException ex) {
			logger.error("Failed to open document - I/O exception", ex);
			Dialogs.showExceptionDialog(window, ex);
		}
		return null;
	}

	/**
	 * Tries to {@link #openDocument(OpenDocumentDescriptor) open} a
	 * {@link Document document}. If this operation fails a
	 * dialog with the description of an error is shown.
	 * @param descriptor the descriptor of a document to open
	 * @return true if the operation is successful, false otherwise
	 */
	public Document maybeOpenDocument(OpenDocumentDescriptor descriptor) {
		return this.maybeOpenDocument(descriptor, null);
	}

	/**
	 * Tries to close a {@link Document document}.
	 * <ul>
	 * <li>checks if the document is saved and if not asks the user what
	 * to do,</li>
	 * <li>Checks if the documents dependent on a given document are closed and
	 * if not:
	 * <ul>
	 * <li>if {@code force} is set - closes them</li>
	 * <li>if {@code force} is not set - asks user what to do:
	 * <ul>
	 * <li>if user answers to proceed - closes the documents</li>
	 * <li>if user answers to cancel - returns false</li>
	 * </ul></li></ul>
	 * <li>closes the document</li>
	 * </ul>
	 * @param document the document to close
	 * @param saveAsOnly if the document should be saved in a new file
	 * (even if it has a backing file)
	 * @param force {@code true} if the dependent documents should be closed
	 * without asking the user, {@code false} otherwise
	 * @return {@code true} if operation is successful, {@code false} if
	 * some document was not saved or some document was not closed
	 * @throws IOException TODO never thrown (??)
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or <br>
	 * if {@link SignalChecksumWorker checksum worker} was interrupted or
	 * failed to calculate the checksum or<br>
	 * TODO when {@link MonitorSignalDocument} throws it
	 */
	public boolean closeDocument(Document document, boolean saveAsOnly, boolean force) throws IOException, SignalMLException {

		synchronized (documentManager) {
			synchronized (document) {

				if (!force) {
					boolean savedOk = assertDocumentIsSaved(document, saveAsOnly, false);
					if (!savedOk) {
						// cancel parent operation
						return false;
					}
				

					String closeString = _("Close");
					String cancelString = _("Cancel");

					int res = showOptionDialog(optionPaneParent,
							_("Are you sure you want to close the preview?"),
							closeString + "?",
							JOptionPane.OK_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							IconUtils.getQuestionIcon(),
							new Object[]{closeString, cancelString},
							closeString
					);
					if (res != 0) return false;
				}


				boolean dependantsOk = assertDocumentDependantsClosed(document, force);
				if (!dependantsOk) {
					// cancel parent operation
					return false;
				}

				closeDocumentInternal(document);

			}
		}

		return true;

	}

	/**
	 * Closes the document and if unsuccessful - shows a dialog explaining
	 * the error.
	 * @param document document to be closed
	 * @return true if the document was closed successfully
	 */
	public boolean closeDocumentAndHandleExceptions(Document document) {
		try {
			closeDocument(document, false, false);
			return true;
		} catch (SignalMLException ex) {
			logger.error("Failed to close document", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return false;
		} catch (IOException ex) {
			logger.error("Failed to close document - i/o exception", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return false;
		}
	}


	/**
	 * Checks if all {@link Document documents} stored in {@link DocumentManager
	 * document manager} are saved and asks the user what to do if they are not.
	 * @return {@code true} if all files are saved (or were saved in this
	 * function) or user answered not to save it,<br>
	 * {@code false} if user aborted the dialog
	 * @throws IOException TODO never thrown (???)
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or <br>
	 * if {@link SignalChecksumWorker checksum worker} was interrupted or
	 * failed to calculate the checksum or<br>
	 * TODO when {@link MonitorSignalDocument} throws it
	 */
	public boolean checkCloseAllDocuments() throws IOException, SignalMLException {

		// Note that this method doesn't check dependencies - either way all documents are closed

		int count;
		Document[] documents;
		int i;
		boolean savedOk;

		synchronized (documentManager) {

			count = documentManager.getDocumentCount();
			documents = new Document[count];
			for (i=0; i<count; i++) {
				documents[i] = documentManager.getDocumentAt(i);
			}

			for (i=0; i<count; i++) {
				synchronized (documents[i]) {
					if (!documents[i].isClosed()) {
						savedOk = assertDocumentIsSaved(documents[i], false, true);
						if (!savedOk) {
							return false;
						}
					}
				}
			}

		}

		return true;

	}

	/**
	 * {@link #closeDocument(Document, boolean, boolean) Closes} all
	 * {@link Document documents} stored in {@link DocumentManager
	 * document manager}.
	 * @throws IOException TODO never thrown (???)
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or <br>
	 * if {@link SignalChecksumWorker checksum worker} was interrupted or
	 * failed to calculate the checksum or<br>
	 * TODO when {@link MonitorSignalDocument} throws it
	 */
	public void closeAllDocuments() throws IOException, SignalMLException {

		// Note that this method doesn't check dependencies - either way all documents are closed

		int count;
		Document[] documents;
		int i;

		synchronized (documentManager) {

			count = documentManager.getDocumentCount();
			documents = new Document[count];
			for (i=0; i<count; i++) {
				documents[i] = documentManager.getDocumentAt(i);
			}

			// all documents have been saved or discarded
			for (i=0; i<count; i++) {
				synchronized (documents[i]) {
					// we ignore possible document modification from another thread
					// because this modification happens after the user has undertaken
					// to close all documents as they were
					if (!documents[i].isClosed()) {
						closeDocument(documents[i], false, true);
					}
				}
			}

		}

	}

	/**
	 * Tries to save a given {@link Document document} and returns if the
	 * operation was successful.
	 * If the document is not savable (is not a {@link MutableDocument})
	 * always true is returned.
	 * <p>
	 * If the document should {@link FileBackedDocument have a backing file}
	 * but there is no or if {@code saveAsOnly} is set, the user is asked
	 * to select the file.
	 * <p>
	 * Executes {@link MutableDocument#saveDocument() saving} operation
	 * in the {@link SaveDocumentWorker worker} and shows the
	 * {@link PleaseWaitDialog wait dialog} until the operation is complete.
	 * @param document the document to be saved
	 * @param saveAsOnly tells if the document should be saved in a new file
	 * @return true if the operation was successful, false otherwise
	 * @throws IOException TODO never thrown (???)
	 * @throws SignalMLException if save worker failed to save the document or
	 * if {@link SignalChecksumWorker checksum worker} was interrupted or
	 * failed to calculate the checksum
	 */
	public boolean saveDocument(Document document, boolean saveAsOnly) throws IOException, SignalMLException {

		if (!document.isSaveable()) {
			return true;
		}

		synchronized (documentManager) {

			synchronized (document) {

				MutableDocument md = (MutableDocument) document;

				if (document instanceof FileBackedDocument) {
					FileBackedDocument fbd = (FileBackedDocument) document;
					File oldFile = fbd.getBackingFile();
					if (saveAsOnly || oldFile == null) {

						ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
						if (type == null) {
							logger.error("Unsupported class [" + document.getClass().getName() + "]");
							throw new ClassCastException();
						}
						FileFilter[] filters = type.getFileFilters();

						boolean hasFile = false;
						File file = null;

						do {

							file = fileChooser.chooseSaveDocument(optionPaneParent, document, filters);
							if (file == null) {
								// file choice canceled
								return false;
							}

							hasFile = true;

							// what if new path is already in the manager?
							Document openDocument = documentManager.getDocumentByFile(file.getAbsoluteFile());
							if (openDocument != null && openDocument != document) {
								OptionPane.showDocumentAlreadyOpenError(optionPaneParent);
								hasFile = false;
							} else {
								// file exists warning
								if (file.exists()) {
									int res = OptionPane.showFileAlreadyExists(optionPaneParent);
									if (res != OptionPane.OK_OPTION) {
										hasFile = false;
									}
								}
							}

						} while (!hasFile);

						if (!Util.equalsWithNulls(oldFile, file)) {
							fbd.setBackingFile(file);
							documentManager.onDocumentPathChange(document, oldFile, file);
						}

					}
				}

				ManagedDocumentType type = ManagedDocumentType.getForClass(md.getClass());
				boolean ok = false;
				if (type != null) {
					if (type.equals(ManagedDocumentType.SIGNAL)) {
						ok = true;
						// do nothing
					} else if (type.equals(ManagedDocumentType.BOOK)) {
						ok = true;
						// do nothing
					} else if (type.equals(ManagedDocumentType.TAG)) {
						ok = onSaveTagDocument((TagDocument) md);
					} else {
						logger.error("Unsupported type [" + type + "]");
						throw new ClassCastException();
					}
				}

				if (!ok) {
					return false;
				}

				SaveDocumentWorker worker = new SaveDocumentWorker(md, pleaseWaitDialog);

				worker.execute();

				pleaseWaitDialog.setActivity(_("saving document"));
				pleaseWaitDialog.configureForIndeterminateSimulated();
				pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

				try {
					worker.get();
				} catch (InterruptedException ex) {
					// ignore
				} catch (ExecutionException ex) {
					logger.error("Worker failed to save", ex.getCause());
					throw new SignalMLException(ex.getCause());
				}

			}

		}

		return true;

	}

	/**
	 * Creates a {@link OpenDocumentDescriptor descriptor} of a file based
	 * on a given {@link MRUDEntry} and uses this descriptor to
	 * {@link #openDocument(OpenDocumentDescriptor) open} a file.
	 * @param mrud the entry describing the file to open
	 * @return the opened {@link Document document} or {@code null} if
	 * <ul>
	 * <li>it is a {@link SignalDocument signal document} but the entry is of
	 * unknown type,</li>
	 * <li>it is a {@link TagDocument tag document} and there is no active
	 * signal</li>
	 * </ul>
	 * @throws IOException if the file doesn't exist or is unreadable
	 * or I/O error occurs while opening RAW signal
	 * @throws SignalMLException if there is no {@link SignalMLCodec codec}
	 * of a given UID or if {@link SaveDocumentWorker save worker}
	 * failed to save the document or if {@link SignalChecksumWorker checksum
	 * worker} was interrupted or failed to calculate the checksum
	 */
	public Document openMRUDEntry(MRUDEntry mrud) throws IOException, SignalMLException {

		ManagedDocumentType type = mrud.getDocumentType();

		OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
		odd.setFile(mrud.getFile());
		odd.setType(type);
		odd.setMakeActive(true);

		if (type.equals(ManagedDocumentType.SIGNAL)) {

			if (mrud instanceof SignalMLMRUDEntry) {

				SignalMLMRUDEntry smlEntry = (SignalMLMRUDEntry) mrud;
				SignalMLDescriptor signalmlDescriptor = smlEntry.getDescriptor();

				SignalMLCodec codec = codecManager.getCodecByUID(signalmlDescriptor.getCodecUID());

				if (codec == null) {
					logger.warn("Mrud codec not found for uid [" + signalmlDescriptor.getCodecUID() + "]");
					throw new MissingCodecException("error.mrudMissingCodecException");
				}
				signalmlDescriptor.setCodec(codec);
				odd.setOpenSignalDescriptor(signalmlDescriptor);
			}
			else if (mrud instanceof RawSignalMRUDEntry) {

				RawSignalMRUDEntry rawEntry = (RawSignalMRUDEntry) mrud;
				odd.setOpenSignalDescriptor(rawEntry.getDescriptor());

			} else {
				logger.error("Don't know how to open this kind of mrud [" + mrud.getClass().getName() + "]");
				return null;
			}

		} else if (type.equals(ManagedDocumentType.BOOK)) {

			// so far nothing special

		} else if (type.equals(ManagedDocumentType.TAG)) {
			OpenTagDescriptor tagOptions = odd.getTagOptions();
			Document activeDocument = actionFocusManager.getActiveDocument();
			if (activeDocument == null || !(activeDocument instanceof SignalDocument)) {
				OptionPane.showNoActiveSignal(optionPaneParent);
				return null;
			}
			tagOptions.setParent((SignalDocument) activeDocument);
		}

		return openDocument(odd);

	}

	/**
	 * Opens a {@link SignalDocument signal document} based on a
	 * {@link OpenSignalMethod method} from the given
	 * {@link OpenDocumentDescriptor descriptor}.
	 * <ul>
	 * <li>if the method is {@code USE_SIGNALML}:
	 * <ul>
	 * <li>{@link OpenSignalDescriptor#getCodec() gets} the
	 * {@link SignalMLCodec codec}</li>
	 * <li>loads a {@link SignalMLDocument document} in a
	 * {@link OpenSignalMLDocumentWorker worker}</li>
	 * <li>sets the size of a block and a page from the descriptor</li>
	 * <li>fills missing parameters of the signal</li>
	 * </ul>
	 * </li>
	 * <li>if the method is {@code RAW}:
	 * <ul>
	 * <li>{@link OpenSignalDescriptor#getRawSignalDescriptor() gets} the
	 * {@link RawSignalDescriptor parameters} of the raw signal,</li>
	 * <li>loads the document in this thread,</li>
	 * </ul></li>
	 * <li>sets to calculate the {@link SignalChecksum checksum} in the
	 * background,</li>
	 * <li>creates a {@link MRUDEntry} and adds it to the
	 * {@link MRUDRegistry cache},</li>
	 * <li>adds a document to the {@link DocumentManager},</li>
	 * <li>if the document {@link OpenDocumentDescriptor#isMakeActive() should}
	 * be set as active does it.</li>
	 * </ul>
	 * @param descriptor the descriptor of the signal to open
	 * @return the created document with the signal (either
	 * {@link SignalMLDocument} or {@link RawSignalDocument})
	 * @throws IOException if the file doesn't exist or is unreadable
	 * or I/O error occurs while opening RAW signal
	 * @throws SignalMLException if the method is unsupported or if
	 * {@link SaveDocumentWorker save worker}
	 * failed to save the document or if {@link SignalChecksumWorker checksum
	 * worker} was interrupted or failed to calculate the checksum
	 * @throws NullPointerException if there is no method or
	 * if the method is {@code USE_SIGNALML} if there is no codec or,
	 * if the method is {@code RAW} if there is no descriptor of a raw signal
	 */
	private SignalDocument openSignalDocument(final OpenDocumentDescriptor descriptor) throws IOException, SignalMLException {

		File file = descriptor.getFile();
		Montage montage = descriptor.getOpenSignalDescriptor().getMontage();

		boolean fileOk = checkOpenedFile(file);
		if (!fileOk) {
			return null;
		}

		AbstractOpenSignalDescriptor openSignalDescriptor = descriptor.getOpenSignalDescriptor();
		if (openSignalDescriptor == null) {
			logger.error("No method");
			throw new NullPointerException();
		}

		if (openSignalDescriptor instanceof SignalMLDescriptor) {

			SignalMLDescriptor signalMLDescriptor = (SignalMLDescriptor) openSignalDescriptor;
			logger.debug("Opening as signal with SignalML");
			final SignalMLCodec codec = signalMLDescriptor.getCodec();
			if (codec == null) {
				Dialogs.showError(_("SignalML Codec not found!"));
				logger.error("No codec");
				return null;
			}

			OpenSignalMLDocumentWorker worker = new OpenSignalMLDocumentWorker(descriptor, pleaseWaitDialog);

			worker.execute();

			pleaseWaitDialog.setActivity(_("opening signal"));
			pleaseWaitDialog.configureForIndeterminateSimulated();
			pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

			logger.debug("Ready to continue");

			SignalMLDocument signalMLDocument = null;
			try {
				signalMLDocument = worker.get();
			} catch (InterruptedException ex) {
				logger.info("OpenSignalMLDocumentWorker interrupted", ex);
			} catch (ExecutionException ex) {
				logger.error("Exception during worker exectution", ex);
				Dialogs.showExceptionDialog((Window) null, ex);
				return null;
			}

			SignalParameters spd = signalMLDescriptor.getSignalParameters();
			if (spd.getPageSize() != null) {
				signalMLDocument.setPageSize(spd.getPageSize());
			}
			if (spd.getBlocksPerPage() != null) {
				signalMLDocument.setBlocksPerPage(spd.getBlocksPerPage());
			}

			boolean infoOk = collectRequiredSignalConfiguration(signalMLDocument, spd);
			if (!infoOk) {
				try {
					signalMLDocument.closeDocument();
				} catch (SignalMLException ex) {
					logger.error("Failed to cleanup document", ex);
				}
				return null;
			}

			// start background checksum calculation
			if (applicationConfig.isPrecalculateSignalChecksums()) {
				SignalChecksumWorker checksummer = new SignalChecksumWorker(signalMLDocument, null, new String[] { "crc32" });
				signalMLDocument.setPrecalculatingWorker(checksummer);
				checksummer.lowerPriority();
				checksummer.execute();
			}

			if (mrudRegistry != null) {
				SignalMLMRUDEntry mrud = new SignalMLMRUDEntry(ManagedDocumentType.SIGNAL, signalMLDocument.getClass(), file.getAbsolutePath(), signalMLDescriptor);
				mrud.setLastTimeOpened(new Date());
				mrudRegistry.registerMRUDEntry(mrud);
			}

			signalMLDocument.setMontage(montage);
			onSignalDocumentAdded(signalMLDocument, descriptor.isMakeActive());
			onCommonDocumentAdded(signalMLDocument);

			if (descriptor.isMakeActive()) {
				actionFocusManager.setActiveDocument(signalMLDocument);
			}

			logger.debug("open end");

			return signalMLDocument;

		} else if (openSignalDescriptor instanceof RawSignalDescriptor) {

			logger.debug("Opening as raw signal");

			RawSignalDescriptor rawDescriptor = (RawSignalDescriptor) openSignalDescriptor;
			if (rawDescriptor == null) {
				logger.error("No descriptor");
				throw new NullPointerException();
			}

			boolean isAsciiSignal = (rawDescriptor.getSourceSignalType() == RawSignalDescriptor.SourceSignalType.ASCII);
			BaseSignalDocument signalDocument =
				isAsciiSignal ? new AsciiSignalDocument(rawDescriptor) : new RawSignalDocument(rawDescriptor);
			signalDocument.setMontage(montage);

			signalDocument.setBackingFile(file);
			signalDocument.openDocument();

			signalDocument.setPageSize(rawDescriptor.getPageSize());
			signalDocument.setBlocksPerPage(rawDescriptor.getBlocksPerPage());

			String videoFilePath = null;
			String videoFileName = null;
			float videoFileOffset = 0;
			if (rawDescriptor.getVideoFileName() != null) {
				// name is relative to signal file path
				File videoFile = new File(file.getParentFile(), rawDescriptor.getVideoFileName());
				Window dialogParent = SvarogApplication.getSharedInstance().getViewerElementManager().getDialogParent();
				if (!videoFile.isFile()) {
					JOptionPane.showMessageDialog(dialogParent, _("Specified video file could not be found. Video preview will not be available."), _("Warning"), JOptionPane.WARNING_MESSAGE);
					// we don't want to display the warning again
					// when this file is re-opened on next Svarog startup
					rawDescriptor.setVideoFileName((String)null);
				} else if (!VideoFrame.isVideoAvailable()) {
					JOptionPane.showMessageDialog(dialogParent, _("<html><body>VLC libraries are missing. Video preview will not be available.<br>The VLC player can be downloaded from http://www.videolan.org/vlc/ or your system's repositiories.</body></html>"), _("Warning"), JOptionPane.WARNING_MESSAGE);
				} else {
					videoFileName = videoFile.getName();
					videoFilePath = videoFile.getPath();
					videoFileOffset = rawDescriptor.getVideoFileOffset();
				}
			}

			// start background checksum calculation
			if (applicationConfig.isPrecalculateSignalChecksums()) {
				SignalChecksumWorker checksummer = new SignalChecksumWorker(signalDocument, null, new String[] { "crc32" });
				signalDocument.setPrecalculatingWorker(checksummer);
				checksummer.lowerPriority();
				checksummer.execute();
			}

			RawSignalMRUDEntry mrud = new RawSignalMRUDEntry(ManagedDocumentType.SIGNAL, signalDocument.getClass(), file.getAbsolutePath(), rawDescriptor);
			mrud.setLastTimeOpened(new Date());
			mrudRegistry.registerMRUDEntry(mrud);

			if (videoFilePath != null && signalDocument instanceof RawSignalDocument) {
				// VideoFrame needs to be created before onCommonDocumentAdded
				// for SignalView to respond properly
				OfflineVideoFrame frame = new OfflineVideoFrame(videoFileName);
				frame.component.open(videoFilePath);
				((RawSignalDocument) signalDocument).setVideoFrame(frame, videoFileOffset);
				frame.setVisible(true);

				if (!frame.component.isSeekable()) {
					Window dialogParent = SvarogApplication.getSharedInstance().getViewerElementManager().getDialogParent();
					JOptionPane.showMessageDialog(dialogParent, _("<html><body>Opened video file has no time index.<br>It may not be possible to change time position.</body></html>"), _("Warning"), JOptionPane.WARNING_MESSAGE);
				}
			}

			onSignalDocumentAdded(signalDocument, descriptor.isMakeActive());
			onCommonDocumentAdded(signalDocument);

			if (descriptor.isMakeActive()) {
				actionFocusManager.setActiveDocument(signalDocument);
			}

			logger.debug("open end");

			return signalDocument;

		} else {
			// other methods are not supported now
			logger.error("Unsupported method [" + openSignalDescriptor.getClass().toString() + "]");
			throw new SignalMLException("error.invalidValue");
		}

	}

	private SignalDocument openMonitorDocument(final OpenDocumentDescriptor descriptor) throws IOException, SignalMLException {

		ExperimentDescriptor monitorOptions = (ExperimentDescriptor) descriptor.getOpenSignalDescriptor();

		monitorOptions.setBackupFrequency(getApplicationConfig().getBackupFrequency());

		MonitorSignalDocument monitorSignalDocument = new MonitorSignalDocument(monitorOptions);
		monitorSignalDocument.setMontage(descriptor.getOpenSignalDescriptor().getMontage());
		monitorSignalDocument.openDocument();

		onSignalDocumentAdded(monitorSignalDocument, descriptor.isMakeActive());
		onCommonDocumentAdded(monitorSignalDocument);

		actionFocusManager.setActiveDocument(monitorSignalDocument);

		((SignalView) monitorSignalDocument.getDocumentView()).setSnapToPageMode(true);
		logger.debug("monitor openned");

		return monitorSignalDocument;

	}

	/**
	 * Opens a {@link BookDocument book document} based on a given
	 * {@link OpenDocumentDescriptor descriptor}:
	 * <ul>
	 * <li>checks if the file is not already open,</li>
	 * <li>opens a document in the {@link OpenBookDocumentWorker worker},</li>
	 * <li>creates a {@link MRUDEntry} and adds it to the
	 * {@link MRUDRegistry cache},</li>
	 * <li>adds a document to the {@link DocumentManager},</li>
	 * <li>if the document {@link OpenDocumentDescriptor#isMakeActive() should}
	 * be set as active does it.</li>
	 * </ul>
	 * @param descriptor the descriptor of the document to open
	 * @return created book document or {@code null} if the opening failed:
	 * <ul>
	 * <li>file is already open and the user doesn't want to replace it or
	 * closing the old file fails</li>
	 * <li>error while opening the file in the worker</li>
	 * </ul>
	 * @throws IOException if the file doesn't exist or is unreadable
	 * @throws SignalMLException if save worker failed to save the document or
	 * if {@link SignalChecksumWorker checksum worker} was interrupted or
	 * failed to calculate the checksum
	 */
	private BookDocument openBookDocument(final OpenDocumentDescriptor descriptor) throws IOException, SignalMLException {

		synchronized (documentManager) {

			BookDocument bookDocument = descriptor.getBookOptions().getExistingDocument();
			if (bookDocument == null) {

				File file = descriptor.getFile();
				boolean fileOk = checkOpenedFile(file);
				if (!fileOk) {
					return null;
				}

				logger.debug("Opening as book");

				OpenBookDocumentWorker worker = new OpenBookDocumentWorker(descriptor, pleaseWaitDialog);

				worker.execute();

				pleaseWaitDialog.setActivity(_("opening book"));
				pleaseWaitDialog.configureForIndeterminateSimulated();
				pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

				try {
					bookDocument = worker.get();
				} catch (InterruptedException ex) {
					logger.info("Worker interrupted", ex);
				} catch (ExecutionException ex) {
					logger.error("Exception during worker exectution", ex);
					Dialogs.showExceptionDialog((Window) null, ex);
					return null;
				}

				if (mrudRegistry != null) {
					MRUDEntry mrud = new MRUDEntry(ManagedDocumentType.BOOK, bookDocument.getClass(), file.getAbsolutePath());
					mrud.setLastTimeOpened(new Date());
					mrudRegistry.registerMRUDEntry(mrud);
				}

			} else {
				logger.debug("Opening given document");
			}

			onBookDocumentAdded(bookDocument, descriptor.isMakeActive());
			onCommonDocumentAdded(bookDocument);

			if (descriptor.isMakeActive()) {
				actionFocusManager.setActiveDocument(bookDocument);
			}

			logger.debug("open end");

			return bookDocument;

		}

	}

	/**
	 * Opens a {@link TagDocument tag document} based on a given
	 * {@link OpenDocumentDescriptor descriptor}:
	 * <ul>
	 * <li>checks if the file is not already open,</li>
	 * <li>if there is no document in the descriptor reads it in the
	 * {@link OpenBookDocumentWorker worker},</li>
	 * <li>{@link #getSignalCheckSum(SignalDocument, String) calculates} the
	 * {@link SignalChecksum checksum} of the parent signal</li>
	 * <li>checks if a {@link Montage montage} of the tag document is compatible
	 * with the montage of the parent signal. If it is not, asks the user what
	 * to do,</li>
	 * <li>creates a {@link MRUDEntry} and adds it to the
	 * {@link MRUDRegistry cache},</li>
	 * <li>adds a document to the {@link DocumentManager},</li>
	 * <li>if the document {@link OpenDocumentDescriptor#isMakeActive() should}
	 * be set as active does it.</li>
	 * </ul>
	 * @param descriptor the descriptor of the document to open
	 * @return the created tag document or {@code null} if the operation was
	 * not successful
	 * @throws IOException if the file doesn't exist or is unreadable
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or if {@link SignalChecksumWorker checksum
	 * worker} was interrupted or failed to calculate the checksum
	 */
	private TagDocument openTagDocument(OpenDocumentDescriptor descriptor) throws IOException, SignalMLException {

		synchronized (documentManager) {

			SignalDocument parent = descriptor.getTagOptions().getParent();
			if (parent == null) {
				throw new NullPointerException("No parent");
			}

			TagDocument tagDocument = descriptor.getTagOptions().getExistingDocument();
			if (tagDocument == null) {

				File file = descriptor.getFile();
				boolean fileOk = checkOpenedFile(file);
				if (!fileOk) {
					return null;
				}

				logger.debug("Opening as XML tag");

				OpenTagDocumentWorker worker = new OpenTagDocumentWorker(descriptor, pleaseWaitDialog);

				worker.execute();

				pleaseWaitDialog.setActivity(_("opening tag"));
				pleaseWaitDialog.configureForIndeterminateSimulated();
				pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

				try {
					tagDocument = worker.get();
				} catch (InterruptedException ex) {
					// ignore
				} catch (ExecutionException ex) {
					logger.error("Exception during worker exectution", ex);
					Dialogs.showExceptionDialog((Window) null, ex);
					return null;
				}

				if (mrudRegistry != null) {
					MRUDEntry mrud = new MRUDEntry(ManagedDocumentType.TAG, tagDocument.getClass(), file.getAbsolutePath());
					mrud.setLastTimeOpened(new Date());
					mrudRegistry.registerMRUDEntry(mrud);
				}

			} else {
				logger.debug("Opening given document");
			}

			TagSignalIdentification tagSignalIdentification = tagDocument.getTagSet().getTagSignalIdentification();
			SignalChecksum tagChecksum = (tagSignalIdentification != null ? tagSignalIdentification.getChecksum() : null);

			if (tagSignalIdentification == null) {

				logger.debug("Tag has no file identification");

			} else {

				logger.debug("Tag identifies as filename [" + tagSignalIdentification.getFileName() + "] format [" + tagSignalIdentification.getFormatId() + "]");

				if (tagChecksum != null) {

					String checksumMethod = tagChecksum.getMethod();
					SignalChecksum parentChecksum = getSignalCheckSum(parent, checksumMethod);

					String tagChecksumValue = tagChecksum.getValue();
					String parentChecksumValue = parentChecksum.getValue();
					logger.debug("Tag has checksum [" + checksumMethod + "] value [" + tagChecksumValue + "] signal checksum [" + parentChecksumValue + "]");
					if (!tagChecksumValue.equalsIgnoreCase(parentChecksumValue)) {
						logger.debug("Checksum different");
						int res = OptionPane.showTagChecksumBad(optionPaneParent);
						if (res != OptionPane.OK_OPTION) {
							return null;
						}
						logger.debug("User elected to proceed");
					}

				}

			}

			Montage tagMontage = tagDocument.getTagSet().getMontage();
			if (tagMontage != null) {
				Montage parentMontage = parent.getMontage();
				if (!tagMontage.isCompatible(parentMontage)) {

					int ans = OptionPane.showMontageDifferentOnTagLoad(optionPaneParent);
					// interpret ans as "whether to load montage from file"
					switch (ans) {

					case OptionPane.YES_OPTION :
						parent.setMontage(tagMontage);
						break;

					case OptionPane.NO_OPTION :
						// nothing to do
						break;

					default :
						// cancel loading
						return null;

					}

				}

			}

			// this adds to tag list
			tagDocument.setParent(parent);
			parent.setPageSize(tagDocument.getPageSize());
			parent.setBlocksPerPage(tagDocument.getBlocksPerPage());

			onTagDocumentAdded(tagDocument, descriptor.isMakeActive());
			onCommonDocumentAdded(tagDocument);

			return tagDocument;

		}
	}

	/**
	 * Closes the given {@link Document document}. Calls methods specific
	 * for different {@link ManagedDocumentType document types}:
	 * <ul>
	 * <li>for a {@link SignalDocument} -
	 * {@link #closeSignalDocument(SignalDocument)}</li>
	 * <li>for a {@link BookDocument} -
	 * {@link #closeBookDocument(BookDocument)}</li>
	 * <li>for a {@link TagDocument} -
	 * {@link #closeTagDocument(TagDocument)}</li>
	 * </ul>
	 * @param document the document to be closed
	 * @throws IOException TODO never thrown
	 * @throws SignalMLException TODO when {@link MonitorSignalDocument} throws
	 * it
	 */
	private void closeDocumentInternal(Document document) throws IOException, SignalMLException {

		if (document instanceof SignalDocument) {
			closeSignalDocument((SignalDocument) document);
		} else if (document instanceof BookDocument) {
			closeBookDocument((BookDocument) document);
		} else if (document instanceof TagDocument) {
			closeTagDocument((TagDocument) document);
		} else {
			logger.error("Unsupported class [" + document.getClass().getName() + "]");
			throw new ClassCastException();
		}

		document.closeDocument();

	}

	/**
	 * Checks if the file is not already open.
	 * If it is asks user what to do.
	 * @param file the file to be checked
	 * @return {@code true} if the file is not open yet or <br>
	 * if it is open, but the users tells to replace it and closing old file is
	 * successful<br>
	 * {@code false} otherwise
	 * @throws IOException if the file doesn't exist or is unreadable
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or if {@link SignalChecksumWorker checksum
	 * worker} was interrupted or failed to calculate the checksum
	 */
	private boolean checkOpenedFile(File file) throws IOException, SignalMLException {

		if (file == null) {
			logger.error("No file to open");
			throw new NullPointerException();
		}

		logger.debug("Request to open signal file [" + file.getAbsolutePath() + "]");

		Document alreadyOpenedDocument = documentManager.getDocumentByFile(file.getAbsoluteFile());
		if (alreadyOpenedDocument != null) {
			// this document is already open, we should ask the user what to do
			int res = OptionPane.showDocumentAlreadyOpened(optionPaneParent);
			if (res == OptionPane.OK_OPTION) {
				boolean closedOk = closeDocument(alreadyOpenedDocument, true, false);
				if (!closedOk) {
					return false;
				}
			} else {
				return false;
			}
		}

		if (!file.exists() || !file.canRead()) {
			logger.error("File doesn't exist or is unreadable");
			throw new FileNotFoundException();
		}

		return true;

	}


	/**
	 * Performs operations necessary when a {@link Document document} is added:
	 * <ul>
	 * <li>adds the document to {@link #documentManager}.</li>
	 * </ul>
	 * @param document the added document
	 */
	private void onCommonDocumentAdded(Document document) {

		documentManager.addDocument(document);

	}

	/**
	 * Performs operations necessary when a {@link SignalDocument signal
	 * document} is added:
	 * <ul>
	 * <li>adds a default {@link Montage montage} to this document if it exists
	 * and {@link ApplicationConfiguration#isAutoLoadDefaultMontage() should be
	 * added}</li>
	 * </ul>
	 * @param document the added document
	 * @param makeActive TODO not used
	 */
	private void onSignalDocumentAdded(SignalDocument document, boolean makeActive) {

		if (applicationConfig.isAutoLoadDefaultMontage()) {
			Montage defaultMontage = (Montage) montagePresetManager.getDefaultPreset();
			if (defaultMontage != null) {
				if (defaultMontage.isCompatible(document)) {
					document.setMontage(defaultMontage);
				} else {
					OptionPane.showDefaultMontageNotCompatible(optionPaneParent);
				}
			}
		}

	}

	/**
	 * Performs operations necessary when a {@link BookDocument book document}
	 * is added - does nothing.
	 * @param document the added document
	 * @param makeActive TODO not used
	 */
	private void onBookDocumentAdded(BookDocument document, boolean makeActive) {
		Document activeDocument = this.actionFocusManager.getActiveDocument();

		if (activeDocument != null) {
			DocumentView documentView = activeDocument.getDocumentView();
			if (documentView instanceof BookView) {
				((BookView) documentView).saveSettingsToApplicationConfiguration();
			}
		}
	}

	/**
	 * Performs operations necessary when a {@link TagDocument tag document}
	 * is added:
	 * <ul>
	 * <li>if {@code makeActive} is {@code true} sets the tag document as an
	 * active tag document in the {@link SignalView signal view}</li>
	 * </ul>
	 * @param document the added tag document
	 * @param makeActive tells if the tag document should be set as an active
	 * tag document
	 */
	private void onTagDocumentAdded(TagDocument document, boolean makeActive) {

		if (makeActive) {
			SignalDocument parent = document.getParent();
			SignalView signalView = (SignalView) parent.getDocumentView();
			if (!signalView.isComparingTags()) {
				parent.setActiveTag(document);
			}
			signalView.repaint();
		}

	}

	/**
	 * Copies the information about the {@link SignalDocument signal}
	 * {@link TagDocument#getParent() parent} to this {@link TagDocument tag
	 * document} to the {@link StyledTagSet set} of {@link Tag tags}
	 * for this document.
	 * These information include:
	 * <ul>
	 * <li>the name of the format in which the signal was stored</li>
	 * <li>the path to the file in which the signal is stored</li>
	 * <li>the crc32 {@link SignalChecksum checksum} of the signal</li>
	 * <li>the default {@link Montage} for the signal if it is compatible,
	 * if it is not asks user what to do</li>
	 * </ul>
	 * @param tagDocument the tag document that is saved
	 * @return if the operation was successful:<br>
	 * {@code false} if the montage in the set is not compatible with
	 * the default montage for the signal and the user cancels the dialog,
	 * {@code true} otherwise
	 * @throws SignalMLException see
	 * {@link #getSignalCheckSum(SignalDocument, String)}
	 */
	private boolean onSaveTagDocument(TagDocument tagDocument) throws SignalMLException {

		SignalDocument parent = tagDocument.getParent();

		TagSignalIdentification tagSignalIdentification = tagDocument.getTagSet().getTagSignalIdentification();
		SignalChecksum tagChecksum = (tagSignalIdentification != null ? tagSignalIdentification.getChecksum() : null);

		if (tagSignalIdentification == null) {
			logger.debug("Tag has no file identification");
			tagSignalIdentification = new TagSignalIdentification();
			tagSignalIdentification.setFormatId(parent.getFormatName());
			if (parent instanceof FileBackedDocument) {
				tagSignalIdentification.setFileName(((FileBackedDocument) parent).getBackingFile().getName());
			}
			tagDocument.getTagSet().setTagSignalIdentification(tagSignalIdentification);
		} else {
			logger.debug("Tag identifies as filename [" + tagSignalIdentification.getFileName() + "] format [" + tagSignalIdentification.getFormatId() + "]");
		}

		if (tagChecksum == null) {

			tagChecksum = getSignalCheckSum(parent, "crc32");
			tagSignalIdentification.setChecksum(tagChecksum);
			logger.debug("Tag had no checksum, set signal checksum of [" + tagChecksum.getValue() + "]");

		}

		// save montage or montage info with tag
		StyledTagSet tagSet = tagDocument.getTagSet();
		if (applicationConfig.isSaveFullMontageWithTag()) {

			Montage existingMontage = tagSet.getMontage();
			Montage parentMontage = parent.getMontage();

			tagSet.setMontageInfo(null);

			if (existingMontage == null) {
				tagSet.setMontage(parentMontage);
			}
			else if (!existingMontage.isCompatible(parentMontage)) {
				int ans = OptionPane.showMontageDifferentOnTagSave(optionPaneParent);
				// interpret ans as "whether to keep original montage"
				switch (ans) {

				case OptionPane.YES_OPTION :
					// nothing to do
					break;

				case OptionPane.NO_OPTION :
					tagSet.setMontage(parentMontage);
					break;

				default :
					// cancel saving
					return false;

				}
			} else {
				// compatible montage - silently freshen it to include any reordering, label changes etc.
				tagSet.setMontage(parentMontage);
			}

		} else {
			tagSet.setMontage(null);
			tagSet.setMontageInfo(parent.getMontageInfo());
		}

		return true;

	}

	/**
	 * Calculates the 'crc32' {@link SignalChecksum checksum} of the given
	 * signal.
	 * Calculation is done by the {@link SignalChecksumWorker worker} and
	 * during the calculation {@link PleaseWaitDialog wait dialog} is shown.
	 * @param parent the signal of which the checksum is to be calculated
	 * @param string TODO never used
	 * @return the calculated checksum
	 * @throws SignalMLException if the calculation was interrupted or
	 * {@link SignalChecksumWorker worker} failed to calculate the checksum
	 */
	private SignalChecksum getSignalCheckSum(SignalDocument parent, String string) throws SignalMLException {

		SignalChecksumWorker checksummer = null;

		// wait for the precalculating worker
		if (parent instanceof AbstractFileSignal) {

			checksummer = ((AbstractFileSignal) parent).getPrecalculatingWorker();
			if (checksummer != null) {

				if (!checksummer.isDone()) {

					checksummer.setPleaseWaitDialog(pleaseWaitDialog);

					pleaseWaitDialog.setActivity(_("calculating checksum"));
					if (parent instanceof FileBackedDocument) {
						File file = ((FileBackedDocument) parent).getBackingFile();
						pleaseWaitDialog.configureForDeterminate(0, (int) file.length(), (int) checksummer.getBytesProcessed());
					} else {
						pleaseWaitDialog.configureForIndeterminate();
					}
					checksummer.normalPriority();
					pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 0, checksummer);

				}

				((AbstractFileSignal) parent).setPrecalculatingWorker(null);

			}

		}

		checksummer = new SignalChecksumWorker(parent, pleaseWaitDialog, new String[] { "crc32" });
		checksummer.execute();

		pleaseWaitDialog.setActivity(_("calculating checksum"));
		if (parent instanceof FileBackedDocument) {
			File file = ((FileBackedDocument) parent).getBackingFile();
			pleaseWaitDialog.configureForDeterminate(0, (int) file.length(), (int) checksummer.getBytesProcessed());
		} else {
			pleaseWaitDialog.configureForIndeterminate();
		}
		pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, checksummer);

		SignalChecksum[] checksums;
		try {
			checksums = checksummer.get();
		} catch (InterruptedException ex) {
			logger.error("Worker interrupted");
			throw new SignalMLException(ex);
		} catch (ExecutionException ex) {
			logger.error("Worker failed to calculate checksum", ex.getCause());
			throw new SignalMLException(ex.getCause());
		}

		return checksums[0];

	}

	/**
	 * Closes the {@link SignalDocument signal document}. Removes it from the
	 * {@link #documentManager document manager}.
	 * @param document the document to be closed
	 */
	private void closeSignalDocument(SignalDocument document) {

		onCommonDocumentRemoved(document);
		onSignalDocumentRemoved(document);

	}

	/**
	 * Closes the {@link BookDocument book document}. Removes it from the
	 * {@link #documentManager document manager}.
	 * @param document the document to be closed
	 */
	private void closeBookDocument(BookDocument document) {

		onCommonDocumentRemoved(document);
		onBookDocumentRemoved(document);

	}

	/**
	 * Closes the {@link TagDocument tag document}. Removes it from the
	 * {@link #documentManager document manager} and sets its parent to
	 * {@code null}.
	 * @param document the document to be closed
	 */
	private void closeTagDocument(TagDocument document) {

		onCommonDocumentRemoved(document);
		onTagDocumentRemoved(document);

	}

	/**
	 * Performs operations necessary when a {@link Document document} is
	 * removed:
	 * <ul>
	 * <li>removes a given {@link Document document} from a
	 * {@link #documentManager document manager}</li>
	 * </ul>
	 * @param document the document to be removed
	 */
	private void onCommonDocumentRemoved(Document document) {

		documentManager.removeDocument(document);

	}

	/**
	 * Performs operations necessary when a {@link SignalDocument signal
	 * document} is removed - nothing to do.
	 * @param document the removed document
	 */
	private void onSignalDocumentRemoved(SignalDocument document) {
		// nothing to do
	}

	/**
	 * Performs operations necessary when a {@link BookDocument book
	 * document} is removed - nothing to do.
	 * @param document the removed document
	 */
	private void onBookDocumentRemoved(BookDocument document) {
		// nothing to do
	}

	/**
	 * Performs operations necessary when a {@link TagDocument tag
	 * document} is removed:
	 * <ul>
	 * <li>{@link TagDocument#setParent(SignalDocument) sets} the parent
	 * document for this tag document to {@code null}</li>
	 * </ul>
	 * @param document the removed document
	 */
	private void onTagDocumentRemoved(TagDocument document) {

		SignalDocument parent = document.getParent();
		SignalView signalView = (SignalView) parent.getDocumentView();
		signalView.repaint();

		// this removes from tag list
		document.setParent(null);
	}

	/**
	 * Checks if the {@link Document document} is saved and if not asks
	 * the user what to do.
	 * If the user:
	 * <ul>
	 * <li>answers to save a document - it is
	 * {@link #saveDocument(Document, boolean) saved} and {@code true}
	 * is returned</li>
	 * <li>answers to not save a document - if {@code closeOnDiscard} is true
	 * it is {@link #closeDocument(Document, boolean, boolean) closed} and
	 * {@code true} is returned</li>
	 * <li>aborts the dialog - {@code false} is returned</li>
	 * </ul>
	 * @param document the document to be checked
	 * @param saveAsOnly if the document should be saved in a new file (even if
	 * it has a backing file)
	 * @param closeOnDiscard true if the document should be closed when
	 * user decides not to save it, false otherwise
	 * @return true if the document is (or was in this function) either saved
	 * or the user decided not to save it, false otherwise
	 * @throws IOException TODO never thrown (???)
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or <br>
	 * if {@link SignalChecksumWorker checksum worker} was interrupted or
	 * failed to calculate the checksum or<br>
	 * TODO when {@link MonitorSignalDocument} throws it
	 */
	private boolean assertDocumentIsSaved(Document document, boolean saveAsOnly, boolean closeOnDiscard) throws IOException, SignalMLException {

		boolean ok;

		if (document.isSaveable()) {
			MutableDocument md = (MutableDocument) document;
			if (!md.isSaved()) {

				// display document modification query
				int res = OptionPane.showDocumentUnsaved(optionPaneParent,md);
				if (res == OptionPane.YES_OPTION) {
					// the user elected to save
					ok = saveDocument(document, saveAsOnly);
				} else if (res == OptionPane.NO_OPTION) {
					// the user elected to discard
					if (closeOnDiscard) {
						closeDocument(document, false, true);
					}
					ok = true;
				} else {
					// canceled etc. - abort
					ok = false;
				}

			} else {
				// the document is saved
				ok = true;
			}
		} else {
			// documents of this type need not be saved
			ok = true;
		}

		return ok;

	}

	/**
	 * Checks if the {@link Document documents} dependent on a given document
	 * are closed. If they are not:
	 * <ul>
	 * <li>if {@code force} is set - #closes them</li>
	 * <li>if {@code force} is not set - asks user what to do:
	 * <ul>
	 * <li>if user answers to proceed - closes the documents</li>
	 * <li>if user answers to cancel - returns false</li>
	 * </ul></li>
	 * </ul>
	 * If the dependent documents are successfully saved and have no dependent
	 * documents they are closed and {@code true} is returned,
	 * if one these is not true {@code false} is returned.
	 * @param document the document to be checked
	 * @param force true if the dependent documents should be closed without
	 * asking the user, false otherwise
	 * @return {@code true} if the dependent documents are closed or were
	 * closed in this function,<br>
	 * {@code false} otherwise
	 * @throws IOException TODO never thrown
	 * @throws SignalMLException if {@link SaveDocumentWorker save worker}
	 * failed to save the document or <br>
	 * if {@link SignalChecksumWorker checksum worker} was interrupted or
	 * failed to calculate the checksum or<br>
	 * TODO when {@link MonitorSignalDocument} throws it
	 */
	private boolean assertDocumentDependantsClosed(Document document, boolean force) throws IOException, SignalMLException {

		List<Document> childDocuments;
		Document childDocument;

		childDocuments = document.getDependentDocuments();
		if (!childDocuments.isEmpty()) {
			List<Document> toClose = new LinkedList<Document>();
			Iterator<Document> it = childDocuments.iterator();
			boolean savedOk;
			boolean dependantsOk;

			// check them
			while (it.hasNext()) {

				childDocument = it.next();

				synchronized (childDocument) {
					if (!force) {
						savedOk = assertDocumentIsSaved(childDocument, false, false);
						if (!savedOk) {
							// cancel parent operation
							return false;
						}
					}

					dependantsOk = assertDocumentDependantsClosed(childDocument, force);
					if (!dependantsOk) {
						// cancel parent operation
						return false;
					}
				}

				toClose.add(childDocument);

			}

			// close them
			it = toClose.iterator();
			while (it.hasNext()) {
				childDocument = it.next();
				synchronized (childDocument) {
					closeDocumentInternal(childDocument);
				}
			}
		}
		return true;
	}

	/**
	 * Fills the {@link SignalParameters descriptor} with the data
	 * from the given {@link SignalMLDocument document}:
	 * <ul>
	 * <li>boolean if the calibration is supported,</li>
	 * <li>boolean if the number of channels is supported and that number,</li>
	 * <li>boolean if the sampling frequency is supported and that frequency.
	 * </li>
	 * </ul>
	 * If some of the parameters are not supported shows the
	 * {@link SignalParametersDialog dialog} to the user and asks him about
	 * them.
	 * @param signalMLDocument the document
	 * @param spd the descriptor of the signal parameters
	 * @return {@code true} if the dialog shown to user was closed with OK
	 * or there was no need to show that dialog,<br>
	 * {@code false} if the dialog was closed with CANCEL
	 */
	private boolean collectRequiredSignalConfiguration(SignalMLDocument signalMLDocument, SignalParameters spd) {

		spd.setCalibrationGain(null);
		if (signalMLDocument.isCalibrationCapable()) {
			spd.setCalibrationEditable(true);
		} else {
			spd.setCalibrationEditable(false);
		}

		if (signalMLDocument.isChannelCountCapable()) {
			spd.setChannelCount(signalMLDocument.getChannelCount());
			spd.setChannelCountEditable(false);
		} else {
			spd.setChannelCount(0);
			spd.setChannelCountEditable(true);
		}

		if (!signalMLDocument.isCalibrationCapable() || !signalMLDocument.isSamplingFrequencyCapable() || !signalMLDocument.isChannelCountCapable()) {

			// additional configuration required
			boolean ok = signalParametersDialog.showDialog(spd, true);
			if (!ok) {
				return false;
			}
			if (spd.isChannelCountEditable()) {
				signalMLDocument.setChannelCount(spd.getChannelCount());
			}
			if (spd.isCalibrationEditable()) {
				signalMLDocument.setCalibration(spd.getCalibrationGain()[0]);
			}

		}

		return true;

	}

	/**
	 * Returns the {@link DocumentManager manager} of {@link Document
	 * documents} in Svarog.
	 * @return the manager of documents in Svarog
	 */
	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	/**
	 * Sets the {@link DocumentManager manager} of {@link Document documents}
	 * in Svarog.
	 * @param documentManager the manager of documents in Svarog
	 */
	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	/**
	 * Returns the {@link MRUDRegistry cache} of {@link MRUDEntry
	 * file descriptions}.
	 * @return the cache of file descriptions
	 */
	public MRUDRegistry getMrudRegistry() {
		return mrudRegistry;
	}

	/**
	 * Sets the {@link MRUDRegistry cache} of {@link MRUDEntry file
	 * descriptions}.
	 * @param mrudRegistry the cache of file descriptions
	 */
	public void setMrudRegistry(MRUDRegistry mrudRegistry) {
		this.mrudRegistry = mrudRegistry;
	}

	/**
	 * Returns the manager of {@link SignalMLCodec codecs}.
	 * @return the manager of codecs
	 */
	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	/**
	 * Sets the manager of {@link SignalMLCodec codecs}.
	 * @param codecManager the manager of codecs
	 */
	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	/**
	 * Returns the parent pane to all dialogs shown by this integrator.
	 * @return the parent pane to all dialogs shown by this integrator
	 */
	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	/**
	 * Sets the parent pane to all dialogs shown by this integrator.
	 * @param optionPaneParent the parent pane to all dialogs shown by this
	 * integrator
	 */
	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

	/**
	 * Returns the {@link ViewerFileChooser chooser} for files in Svarog.
	 * @return the chooser for files in Svarog
	 */
	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Sets the {@link ViewerFileChooser chooser} for files in Svarog.
	 * @param fileChooser the chooser for files in Svarog
	 */
	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	/**
	 * Returns the {@link SignalParametersDialog dialog} to get parameters of
	 * the signal.
	 * @return the dialog to get parameters of the signal
	 */
	public SignalParametersDialog getSignalParametersDialog() {
		return signalParametersDialog;
	}

	/**
	 * Sets the {@link SignalParametersDialog dialog} to get parameters of the
	 * signal.
	 * @param signalParametersDialog the dialog to get parameters of the signal
	 */
	public void setSignalParametersDialog(SignalParametersDialog signalParametersDialog) {
		this.signalParametersDialog = signalParametersDialog;
	}

	/**
	 * Returns the {@link ActionFocusManager manager} of active elements in
	 * Svarog.
	 * @return the manager of active elements in Svarog
	 */
	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	/**
	 * Sets the {@link ActionFocusManager manager} of active elements in Svarog.
	 * @param actionFocusManager the manager of active elements in Svarog
	 */
	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	/**
	 * Returns the {@link ApplicationConfiguration configuration} of Svarog.
	 * @return the configuration of Svarog
	 */
	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	/**
	 * Sets the {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	/**
	 * Returns the {@link MontagePresetManager manager} of montage presets.
	 * @return the manager of montage presets
	 */
	public MontagePresetManager getMontagePresetManager() {
		return montagePresetManager;
	}

	/**
	 * Sets the {@link MontagePresetManager manager} of montage presets.
	 * @param montagePresetManager the manager of montage presets
	 */
	public void setMontagePresetManager(MontagePresetManager montagePresetManager) {
		this.montagePresetManager = montagePresetManager;
	}

	/**
	 * Returns the {@link PleaseWaitDialog dialog} that tells the user to wait.
	 * @return the dialog that tells the user to wait
	 */
	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	/**
	 * Sets the {@link PleaseWaitDialog dialog} that tells the user to wait.
	 * @param pleaseWaitDialog the dialog that tells the user to wait
	 */
	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

}
