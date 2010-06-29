/* DocumentFlowIntegrator.java created 2007-09-19
 *
 */

package org.signalml.app.document;

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

import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.model.OpenTagDescriptor;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.model.OpenSignalDescriptor.OpenSignalMethod;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.dialog.SignalParametersDialog;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.OpenBookDocumentWorker;
import org.signalml.app.worker.OpenSignalMLDocumentWorker;
import org.signalml.app.worker.OpenTagDocumentWorker;
import org.signalml.app.worker.SaveDocumentWorker;
import org.signalml.app.worker.SignalChecksumWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagSignalIdentification;
import org.signalml.exception.MissingCodecException;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;

/** DocumentFlowIntegrator
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DocumentFlowIntegrator {

	protected static final Logger logger = Logger.getLogger(DocumentFlowIntegrator.class);

	private MessageSourceAccessor messageSource;

	private DocumentManager documentManager;
	private MRUDRegistry mrudRegistry;
	private SignalMLCodecManager codecManager;
	private Component optionPaneParent = null;
	private ViewerFileChooser fileChooser;

	private SignalParametersDialog signalParametersDialog;

	private ActionFocusManager actionFocusManager;
	private ApplicationConfiguration applicationConfig;
	private MontagePresetManager montagePresetManager;

	private PleaseWaitDialog pleaseWaitDialog;

	public Document openDocument(OpenDocumentDescriptor descriptor) throws IOException, SignalMLException {

		ManagedDocumentType type = descriptor.getType();
		if (type.equals(ManagedDocumentType.SIGNAL)) {
			return openSignalDocument(descriptor);
		} else if (type.equals(ManagedDocumentType.BOOK)) {
			return openBookDocument(descriptor);
		} else if (type.equals(ManagedDocumentType.TAG)) {
			return openTagDocument(descriptor);
		} else {
			logger.error("Unsupported type [" + type + "]");
			throw new ClassCastException();
		}

	}

	public boolean closeDocument(Document document, boolean saveAsOnly, boolean force) throws IOException, SignalMLException {

		synchronized (documentManager) {
			synchronized (document) {

				if (!force) {
					boolean savedOk = assertDocumentIsSaved(document, saveAsOnly, false);
					if (!savedOk) {
						// cancel parent operation
						return false;
					}
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

	public boolean saveDocument(Document document, boolean saveAsOnly) throws IOException, SignalMLException {

		if (!(document instanceof MutableDocument)) {
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
						FileFilter[] filters = type.getFileFilters(messageSource);

						boolean hasFile = false;
						File file = null;

						do {

							file = fileChooser.chooseSaveDocument(optionPaneParent, filters);
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

				pleaseWaitDialog.setActivity(messageSource.getMessage("activity.savingFile"));
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

	public boolean saveAllDocuments() throws IOException, SignalMLException {

		boolean allOk = true;
		int count;
		Document document;
		int i;
		boolean savedOk;

		synchronized (documentManager) {

			count = documentManager.getDocumentCount();
			for (i=0; i<count; i++) {
				document = documentManager.getDocumentAt(i);
				synchronized (document) {
					savedOk = saveDocument(document, false);
				}
				allOk &= savedOk;
			}

		}

		return allOk;

	}

	public Document openMRUDEntry(MRUDEntry mrud) throws IOException, SignalMLException {

		ManagedDocumentType type = mrud.getDocumentType();

		OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
		odd.setFile(mrud.getFile());
		odd.setType(type);
		odd.setMakeActive(true);

		if (type.equals(ManagedDocumentType.SIGNAL)) {

			OpenSignalDescriptor signalOptions = odd.getSignalOptions();
			if (mrud instanceof SignalMLMRUDEntry) {

				SignalMLMRUDEntry smlEntry = (SignalMLMRUDEntry) mrud;
				signalOptions.setMethod(OpenSignalMethod.USE_SIGNALML);
				SignalMLCodec codec = codecManager.getCodecByUID(smlEntry.getCodecUID());
				if (codec == null) {
					logger.warn("Mrud codec not found for uid [" + smlEntry.getCodecUID() + "]");
					throw new MissingCodecException("error.mrudMissingCodecException");
				}
				signalOptions.setCodec(codec);
				SignalParameterDescriptor spd = odd.getSignalOptions().getParameters();
				spd.setPageSize(smlEntry.getPageSize());
				spd.setBlocksPerPage(smlEntry.getBlocksPerPage());
				spd.setSamplingFrequency(smlEntry.getSamplingFrequency());
				spd.setChannelCount(smlEntry.getChannelCount());
				spd.setCalibration(smlEntry.getCalibration());

			}
			else if (mrud instanceof RawSignalMRUDEntry) {

				RawSignalMRUDEntry rawEntry = (RawSignalMRUDEntry) mrud;
				signalOptions.setMethod(OpenSignalMethod.RAW);
				signalOptions.setRawSignalDescriptor(rawEntry.getDescriptor());

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

	private SignalDocument openSignalDocument(final OpenDocumentDescriptor descriptor) throws IOException, SignalMLException {

		final File file = descriptor.getFile();
		boolean fileOk = checkOpenedFile(file);
		if (!fileOk) {
			return null;
		}

		OpenSignalDescriptor signalOptions = descriptor.getSignalOptions();
		OpenSignalMethod method = signalOptions.getMethod();
		if (method == null) {
			logger.error("No method");
			throw new NullPointerException();
		}

		if (method.equals(OpenSignalMethod.USE_SIGNALML)) {

			logger.debug("Opening as signal with SignalML");
			final SignalMLCodec codec = signalOptions.getCodec();
			if (codec == null) {
				logger.error("No codec");
				throw new NullPointerException();
			}

			OpenSignalMLDocumentWorker worker = new OpenSignalMLDocumentWorker(descriptor, pleaseWaitDialog);

			worker.execute();

			pleaseWaitDialog.setActivity(messageSource.getMessage("activity.openingSignalFile"));
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
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
				return null;
			}

			SignalParameterDescriptor spd = signalOptions.getParameters();
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
				SignalMLMRUDEntry mrud = new SignalMLMRUDEntry(ManagedDocumentType.SIGNAL, signalMLDocument.getClass(), file.getAbsolutePath(), codec.getSourceUID(), codec.getFormatName());
				mrud.setLastTimeOpened(new Date());
				mrud.setPageSize(spd.getPageSize());
				mrud.setBlocksPerPage(spd.getBlocksPerPage());
				mrud.setSamplingFrequency(spd.getSamplingFrequency());
				mrud.setChannelCount(spd.getChannelCount());
				mrud.setCalibration(spd.getCalibration());
				mrudRegistry.registerMRUDEntry(mrud);
			}

			onSignalDocumentAdded(signalMLDocument, descriptor.isMakeActive());
			onCommonDocumentAdded(signalMLDocument);

			if (descriptor.isMakeActive()) {
				actionFocusManager.setActiveDocument(signalMLDocument);
			}

			logger.debug("open end");

			return signalMLDocument;

		} else if (method.equals(OpenSignalMethod.RAW)) {

			logger.debug("Opening as raw signal");

			RawSignalDescriptor rawDescriptor = signalOptions.getRawSignalDescriptor();
			if (rawDescriptor == null) {
				logger.error("No descriptor");
				throw new NullPointerException();
			}

			RawSignalDocument rawSignalDocument = new RawSignalDocument(
			        signalOptions.getType(),
			        rawDescriptor
			);

			rawSignalDocument.setBackingFile(descriptor.getFile());
			rawSignalDocument.openDocument();

			rawSignalDocument.setPageSize(rawDescriptor.getPageSize());
			rawSignalDocument.setBlocksPerPage(rawDescriptor.getBlocksPerPage());

			// start background checksum calculation
			if (applicationConfig.isPrecalculateSignalChecksums()) {
				SignalChecksumWorker checksummer = new SignalChecksumWorker(rawSignalDocument, null, new String[] { "crc32" });
				rawSignalDocument.setPrecalculatingWorker(checksummer);
				checksummer.lowerPriority();
				checksummer.execute();
			}

			RawSignalMRUDEntry mrud = new RawSignalMRUDEntry(ManagedDocumentType.SIGNAL, rawSignalDocument.getClass(), file.getAbsolutePath(), rawDescriptor);
			mrud.setLastTimeOpened(new Date());
			mrudRegistry.registerMRUDEntry(mrud);

			onSignalDocumentAdded(rawSignalDocument, descriptor.isMakeActive());
			onCommonDocumentAdded(rawSignalDocument);

			if (descriptor.isMakeActive()) {
				actionFocusManager.setActiveDocument(rawSignalDocument);
			}

			logger.debug("open end");

			return rawSignalDocument;

		} else {
			// other methods are not supported now
			logger.error("Unsupported method [" + method + "]");
			throw new SignalMLException("error.invalidValue");
		}

	}

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

				pleaseWaitDialog.setActivity(messageSource.getMessage("activity.openingBookFile"));
				pleaseWaitDialog.configureForIndeterminateSimulated();
				pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

				try {
					bookDocument = worker.get();
				} catch (InterruptedException ex) {
					logger.info("Worker interrupted", ex);
				} catch (ExecutionException ex) {
					logger.error("Exception during worker exectution", ex);
					ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
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

				pleaseWaitDialog.setActivity(messageSource.getMessage("activity.openingTagFile"));
				pleaseWaitDialog.configureForIndeterminateSimulated();
				pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

				try {
					tagDocument = worker.get();
				} catch (InterruptedException ex) {
					// ignore
				} catch (ExecutionException ex) {
					logger.error("Exception during worker exectution", ex);
					ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
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

			onTagDocumentAdded(tagDocument, descriptor.isMakeActive());
			onCommonDocumentAdded(tagDocument);

			return tagDocument;

		}
	}

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

	private void onCommonDocumentAdded(Document document) {

		documentManager.addDocument(document);

	}

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

	private void onBookDocumentAdded(BookDocument document, boolean makeActive) {


	}

	private void onTagDocumentAdded(TagDocument document, boolean makeActive) {

		if (makeActive) {
			SignalDocument parent = document.getParent();
			SignalView signalView = (SignalView) parent.getDocumentView();
			if (!signalView.isComparingTags()) {
				parent.setActiveTag(document);
			}
		}

	}

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

	private SignalChecksum getSignalCheckSum(SignalDocument parent, String string) throws SignalMLException {

		SignalChecksumWorker checksummer = null;

		// wait for the precalculating worker
		if (parent instanceof AbstractFileSignal) {

			checksummer = ((AbstractFileSignal) parent).getPrecalculatingWorker();
			if (checksummer != null) {

				if (!checksummer.isDone()) {

					checksummer.setPleaseWaitDialog(pleaseWaitDialog);

					pleaseWaitDialog.setActivity(messageSource.getMessage("activity.checksummingSignalFile"));
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

		pleaseWaitDialog.setActivity(messageSource.getMessage("activity.checksummingSignalFile"));
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

	private void closeSignalDocument(SignalDocument document) {

		onCommonDocumentRemoved(document);
		onSignalDocumentRemoved(document);

	}

	private void closeBookDocument(BookDocument document) {

		onCommonDocumentRemoved(document);
		onBookDocumentRemoved(document);

	}

	private void closeTagDocument(TagDocument document) {

		onCommonDocumentRemoved(document);
		onTagDocumentRemoved(document);

	}

	private void onCommonDocumentRemoved(Document document) {

		documentManager.removeDocument(document);

	}

	private void onSignalDocumentRemoved(SignalDocument document) {
		// nothing to do
	}

	private void onBookDocumentRemoved(BookDocument document) {
		// nothing to do
	}

	private void onTagDocumentRemoved(TagDocument document) {

		// this removes from tag list
		document.setParent(null);

	}

	private boolean assertDocumentIsSaved(Document document, boolean saveAsOnly, boolean closeOnDiscard) throws IOException, SignalMLException {

		boolean ok;

		if (document instanceof MutableDocument) {
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

	private boolean assertDocumentDependantsClosed(Document document, boolean force) throws IOException, SignalMLException {

		List<Document> childDocuments;
		Document childDocument;

		childDocuments = document.getDependentDocuments();
		if (!childDocuments.isEmpty()) {

			// inform the user that dependent documents must be closed
			int res = 0;
			if (!force) {
				res = OptionPane.showOtherDocumentsDepend(optionPaneParent);
			}
			if (force || res == OptionPane.YES_OPTION) {

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

			} else {
				return false;
			}

		}

		return true;

	}

	private boolean collectRequiredSignalConfiguration(SignalMLDocument signalMLDocument, SignalParameterDescriptor spd) {

		spd.setCalibration(null);
		if (signalMLDocument.isCalibrationCapable()) {
			spd.setCalibrationEditable(true);
		} else {
			spd.setCalibrationEditable(false);
		}

		if (signalMLDocument.isChannelCountCapable()) {
			spd.setChannelCount(signalMLDocument.getChannelCount());
			spd.setChannelCountEditable(false);
		} else {
			spd.setChannelCount(null);
			spd.setChannelCountEditable(true);
		}

		if (signalMLDocument.isSamplingFrequencyCapable()) {
			spd.setSamplingFrequency(signalMLDocument.getSamplingFrequency());
			spd.setSamplingFrequencyEditable(false);
		} else {
			spd.setSamplingFrequency(null);
			spd.setSamplingFrequencyEditable(true);
		}

		if (signalMLDocument.isCalibrationCapable() || !signalMLDocument.isSamplingFrequencyCapable() || !signalMLDocument.isChannelCountCapable()) {

			// additional configuration required

			boolean ok = signalParametersDialog.showDialog(spd, true);
			if (!ok) {
				return false;
			}

			if (spd.isSamplingFrequencyEditable()) {
				signalMLDocument.setSamplingFrequency(spd.getSamplingFrequency());
			}
			if (spd.isChannelCountEditable()) {
				signalMLDocument.setChannelCount(spd.getChannelCount());
			}
			if (spd.isCalibrationEditable()) {
				signalMLDocument.setCalibration(spd.getCalibration());
			}

		}

		return true;

	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public MRUDRegistry getMrudRegistry() {
		return mrudRegistry;
	}

	public void setMrudRegistry(MRUDRegistry mrudRegistry) {
		this.mrudRegistry = mrudRegistry;
	}

	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public SignalParametersDialog getSignalParametersDialog() {
		return signalParametersDialog;
	}

	public void setSignalParametersDialog(SignalParametersDialog signalParametersDialog) {
		this.signalParametersDialog = signalParametersDialog;
	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public MontagePresetManager getMontagePresetManager() {
		return montagePresetManager;
	}

	public void setMontagePresetManager(MontagePresetManager montagePresetManager) {
		this.montagePresetManager = montagePresetManager;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

}
