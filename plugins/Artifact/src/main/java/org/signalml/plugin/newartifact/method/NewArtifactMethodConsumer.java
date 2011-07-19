package org.signalml.plugin.newartifact.method;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.method.Method;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.i18n.PluginMessageSourceManager;
import org.signalml.plugin.method.IPluginMethodResultConsumer;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.newartifact.data.NewArtifactApplicationData;
import org.signalml.plugin.newartifact.data.NewArtifactResult;
import org.signalml.plugin.newartifact.ui.NewArtifactResultDialog;
import org.signalml.plugin.newartifact.ui.NewArtifactResultTargetDescriptor;
import org.signalml.util.Util;

/**
 * ArtifactMethodConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewArtifactMethodConsumer implements IPluginMethodResultConsumer {

	protected static final Logger logger = Logger
					       .getLogger(NewArtifactMethodConsumer.class);

	private Window dialogParent;
	private FileChooser fileChooser;

	private NewArtifactResultDialog resultDialog;

	private PluginMethodManager manager;

	@Override
	public void initialize(PluginMethodManager manager) {
		this.manager = manager;
		this.dialogParent = manager.getSvarogAccess().getGUIAccess().getDialogParent();
		this.fileChooser = manager.getSvarogAccess().getGUIAccess().getFileChooser();

		try {
			this.resultDialog = new NewArtifactResultDialog(
				PluginMessageSourceManager.GetMessageSource(),
				this.dialogParent, true);
		} catch (PluginException e) {
			manager.handleException(e);
			return;
		}
		this.resultDialog.setFileChooser(fileChooser);

	}

	@Override
	public boolean consumeResult(Method method, Object methodData,
				     Object methodResult) throws SignalMLException {

		if (!(methodData instanceof NewArtifactApplicationData)) {
			logger.error("Invalid artifact data");
			return false;
		}

		NewArtifactApplicationData data = (NewArtifactApplicationData) methodData;
		NewArtifactResult result = (NewArtifactResult) methodResult;

		NewArtifactResultTargetDescriptor descriptor = new NewArtifactResultTargetDescriptor();

		SvarogAccessSignal signalAccess = this.manager.getSvarogAccess()
						  .getSignalAccess();

		ExportedSignalDocument signalDocument = data.getSignalDocument();
		boolean signalAvailable;
		if (signalDocument == null || signalDocument.isClosed()) {
			logger.warn("Document unavailable or has been closed");
			signalAvailable = false;
		} else {
			signalAvailable = true;
		}
		descriptor.setSignalAvailable(signalAvailable);

		final File primaryTagFile = result.getTagFile();
		if (primaryTagFile == null || !primaryTagFile.exists()) {
			throw new SignalMLException("No result tag");
		}

		ExportedTagDocument primaryTag = null;
		try {
			primaryTag = new TagDocument(primaryTagFile);
		} catch (IOException e) {
			logger.error("Invalid tag file");
			return false;
		}

		descriptor.setPrimaryTag(primaryTag);

		File workingDirectory = new File(data.getProjectPath(),
						 data.getPatientName());
		File[] additionalTagFiles = workingDirectory
		.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.equals(primaryTagFile)) {
					return false;
				}
				String fileExtension = Util.getFileExtension(pathname,
						       false);
				return (fileExtension != null && "tag"
					.equalsIgnoreCase(fileExtension));
			}

		});
		ArrayList<File> additionalTags = new ArrayList<File>();
		for (File f : additionalTagFiles) {
			additionalTags.add(f);
		}

		descriptor.setAdditionalTags(additionalTags);
		descriptor.setChosenAdditionalTags(new ArrayList<File>());

		descriptor.setPrimaryOpenInWindow(true);
		descriptor.setPrimarySaveToFile(true);

		descriptor.setAdditionalOpenInWindow(false);
		descriptor.setAdditionalSaveToFile(false);

		boolean dialogOk = resultDialog.showDialog(descriptor, true);
		if (!dialogOk) {
			return false;
		}

		if (descriptor.isPrimarySaveToFile()) {
			primaryTag.setBackingFile(descriptor.getPrimaryTagFile());
			try {
				primaryTag.saveDocument();
			} catch (SignalMLException ex) {
				logger.error("Failed to save document", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;
			} catch (IOException ex) {
				logger.error("Failed to save document - i/o exception", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;
			}
		}

		if (signalAvailable && descriptor.isPrimaryOpenInWindow()) {
			try {
				signalAccess.openTagDocument(primaryTag.getBackingFile(),
							     signalDocument, true);
			} catch (InvalidClassException ex) {
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;
			} catch (IOException ex) {
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;
			}
		}

		ArrayList<File> chosenAdditionalTags = descriptor
						       .getChosenAdditionalTags();
		if (!chosenAdditionalTags.isEmpty()) {

			boolean additionalOpenInWindow = descriptor
							 .isAdditionalOpenInWindow();
			boolean additionalSaveToFile = descriptor.isAdditionalSaveToFile();

			if (additionalOpenInWindow || additionalSaveToFile) {
				for (File file : chosenAdditionalTags) {
					StyledTagSet additionalTagSet = null;
					File saveFile;
					boolean hasFile = false;
					TagDocument additionalTag = null;

					if (additionalSaveToFile) {

						TagDocument d;
						try {
							d = new TagDocument(file);
							d.openDocument();
						} catch (IOException ex) {
							ErrorsDialog.showImmediateExceptionDialog(
								dialogParent, ex);
							return false;
						}

						additionalTagSet = d.getTagSet();
						additionalTag = new TagDocument(additionalTagSet);
						d.closeDocument();

						hasFile = false;

						do {
							saveFile = fileChooser.chooseSaveTag(dialogParent);
							if (saveFile == null) {
								// file choice canceled
								break;
							}

							hasFile = true;

							// file exists warning
							if (saveFile.exists()) {
								int res = OptionPane
									  .showFileAlreadyExists(dialogParent);
								if (res != OptionPane.OK_OPTION) {
									hasFile = false;
								}
							}

						} while (!hasFile);

						if (hasFile) {
							additionalTag.setBackingFile(saveFile);
							try {
								additionalTag.saveDocument();
							} catch (SignalMLException ex) {
								logger.error("Failed to save document", ex);
								ErrorsDialog.showImmediateExceptionDialog(
									dialogParent, ex);
								return false;
							} catch (IOException ex) {
								logger.error(
									"Failed to save document - i/o exception",
									ex);
								ErrorsDialog.showImmediateExceptionDialog(
									dialogParent, ex);
								return false;
							}

						}

					}

					if (additionalOpenInWindow) {
						File tagFile = null;
						if (additionalTag != null) {
							tagFile = additionalTag.getBackingFile();
						}
						if (tagFile == null) {
							tagFile = file;
						}

						try {
							signalAccess.openTagDocument(file, signalDocument,
										     false);
						} catch (InvalidClassException ex) {
							ErrorsDialog.showImmediateExceptionDialog(
								dialogParent, ex);
							return false;
						} catch (IOException ex) {
							ErrorsDialog.showImmediateExceptionDialog(
								dialogParent, ex);
							return false;
						}
					}
				}
			}
		}

		return true;
	}
}
