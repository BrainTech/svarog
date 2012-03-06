/* NewStagerMethodConsumer.java created 2008-02-08
 * 
 */

package org.signalml.plugin.newstager.method;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodResultConsumer;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.view.components.dialogs.OptionPane;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.method.Method;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.method.IPluginMethodResultConsumer;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.newstager.data.NewStagerApplicationData;
import org.signalml.plugin.newstager.data.NewStagerResult;
import org.signalml.plugin.newstager.ui.NewStagerResultDialog;
import org.signalml.plugin.newstager.ui.NewStagerResultTargetDescriptor;
import org.signalml.util.Util;

/**
 * NewStagerMethodConsumer
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerMethodConsumer implements IPluginMethodResultConsumer {

	protected static final Logger logger = Logger
			.getLogger(NewStagerMethodConsumer.class);

	private DocumentFlowIntegrator documentFlowIntegrator;
	private Window dialogParent;

	private NewStagerResultDialog resultDialog;

	private LegacyTagImporter legacyTagImporter;

	private PluginMethodManager manager;

	@Override
	public void initialize(PluginMethodManager manager) {
		this.manager = manager;

		this.dialogParent = this.manager.getSvarogAccess().getGUIAccess()
				.getDialogParent();

		this.resultDialog = new NewStagerResultDialog(this.dialogParent, true);

		this.resultDialog.setFileChooser(this.manager.getSvarogAccess()
				.getGUIAccess().getFileChooser());

		this.legacyTagImporter = new LegacyTagImporter();
	}

	@Override
	public boolean consumeResult(Method method, Object methodData,
			Object methodResult) throws SignalMLException {

		if (!(methodData instanceof NewStagerApplicationData)) {
			logger.error("Invalid stager data");
			return false;
		}

		NewStagerApplicationData data = (NewStagerApplicationData) methodData;
		NewStagerResult result = (NewStagerResult) methodResult;

		NewStagerResultTargetDescriptor descriptor = new NewStagerResultTargetDescriptor();

		descriptor.setStagerResult(result);

		SignalDocument signalDocument = null; //TODO! data.getSignalDocument();
		boolean signalAvailable;
		if (signalDocument == null || signalDocument.isClosed()) {
			logger.warn("Document unavailable or has been closed");
			signalAvailable = false;
		} else {
			signalAvailable = true;
		}
		descriptor.setSignalAvailable(signalAvailable);

		MultichannelSampleSource sampleSource = data.getSampleSource();
		int minSampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
		float totalLength = minSampleCount
				/ sampleSource.getSamplingFrequency();
		float segmentLength = data.getPageSize();
		int segmentCount = (int) Math.floor(totalLength / segmentLength);

		descriptor.setSegmentCount(segmentCount);
		descriptor.setSegmentLength(segmentLength);

		final File primaryTagFile = result.getTagFile();
		if (primaryTagFile == null || !primaryTagFile.exists()) {
			throw new SignalMLException("No result tag");
		}

		StyledTagSet primaryTagSet = null;
		try {
			primaryTagSet = legacyTagImporter.importLegacyTags(primaryTagFile,
					sampleSource.getSamplingFrequency());
		} catch (SignalMLException ex) {
			logger.error("Failed to read primary result tag", ex);
			Dialogs.showExceptionDialog(dialogParent, ex);
			return false;
		}
		TagDocument primaryTag = new TagDocument(primaryTagSet);

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
				Dialogs.showExceptionDialog(dialogParent, ex);
				return false;
			} catch (IOException ex) {
				logger.error("Failed to save document - i/o exception", ex);
				Dialogs.showExceptionDialog(dialogParent, ex);
				return false;
			}
		}

		if (signalAvailable && descriptor.isPrimaryOpenInWindow()) {

			OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
			odd.setFile(primaryTag.getBackingFile());
			odd.setMakeActive(true);
			odd.setType(ManagedDocumentType.TAG);
			odd.getTagOptions().setParent(signalDocument);
			odd.getTagOptions().setExistingDocument(primaryTag);

			try {
				documentFlowIntegrator.openDocument(odd);
			} catch (SignalMLException ex) {
				logger.error("Failed to open document", ex);
				Dialogs.showExceptionDialog(dialogParent, ex);
				return false;
			} catch (IOException ex) {
				logger.error("Failed to open document - i/o exception", ex);
				Dialogs.showExceptionDialog(dialogParent, ex);
				return false;
			} catch (ConnectException ex) {
				logger.error("Failed to open document - connection exception",
						ex);
				Dialogs.showExceptionDialog(dialogParent, ex);
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

				StyledTagSet additionalTagSet = null;
				TagDocument additionalTag = null;
				File saveFile;
				boolean hasFile = false;

				for (File file : chosenAdditionalTags) {

					try {
						additionalTagSet = legacyTagImporter.importLegacyTags(
								file, sampleSource.getSamplingFrequency());
					} catch (SignalMLException ex) {
						logger.error("Failed to read additional result tag", ex);
						Dialogs.showExceptionDialog(dialogParent, ex);
						return false;
					}
					additionalTag = new TagDocument(additionalTagSet);

					if (additionalSaveToFile) {

						hasFile = false;

						do {

							saveFile = this.manager.getSvarogAccess()
									.getGUIAccess().getFileChooser()
									.chooseSaveTag(dialogParent);
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
								Dialogs.showExceptionDialog(dialogParent, ex);
								return false;
							} catch (IOException ex) {
								logger.error(
										"Failed to save document - i/o exception",
										ex);
								Dialogs.showExceptionDialog(dialogParent, ex);
								return false;
							}

						}

					}

					if (additionalOpenInWindow) {

						OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
						odd.setFile(additionalTag.getBackingFile());
						odd.setMakeActive(false);
						odd.setType(ManagedDocumentType.TAG);
						odd.getTagOptions().setParent(signalDocument);
						odd.getTagOptions().setExistingDocument(additionalTag);

						try {
							documentFlowIntegrator.openDocument(odd);
						} catch (SignalMLException ex) {
							logger.error("Failed to open document", ex);
							Dialogs.showExceptionDialog(dialogParent, ex);
							return false;
						} catch (IOException ex) {
							logger.error(
									"Failed to open document - i/o exception",
									ex);
							Dialogs.showExceptionDialog(dialogParent, ex);
							return false;
						} catch (ConnectException ex) {
							logger.error(
									"Failed to open document - connection exception",
									ex);
							Dialogs.showExceptionDialog(dialogParent, ex);
							return false;
						}

					}

				}

			}

		}

		return true;

	}

}
