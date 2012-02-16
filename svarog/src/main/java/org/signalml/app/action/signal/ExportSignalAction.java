/* ExportSignalAction.java created 2008-01-27
 *
 */
package org.signalml.app.action.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.components.dialogs.ExportSignalDialog;
import org.signalml.app.view.components.dialogs.OptionPane;
import org.signalml.app.view.components.dialogs.PleaseWaitDialog;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalScanResult;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.app.worker.ScanSignalWorker;
import org.signalml.app.worker.document.ExportSignalWorker;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalDescriptor.SourceSignalType;
import org.signalml.domain.signal.space.MarkerTimeSpace;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.util.Util;

/** ExportSignalAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportSignalAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExportSignalAction.class);

	private ExportSignalDialog exportSignalDialog;
	private PleaseWaitDialog pleaseWaitDialog;
	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	private RawSignalDescriptorWriter descriptorWriter;

	public ExportSignalAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Export Signal..."));
		setToolTip(_("Export signal to simple binary format"));
		setMnemonic(KeyEvent.VK_S);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Export signal");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}
		SignalView signalView = (SignalView) signalDocument.getDocumentView();
		SignalPlot masterPlot = signalView.getMasterPlot();
		TagDocument tagDocument = signalDocument.getActiveTag();

		SignalExportDescriptor signalExportDescriptor;
		Preset preset = exportSignalDialog.getPresetManager().getDefaultPreset();
		if (preset == null) {
			signalExportDescriptor = new SignalExportDescriptor();
		} else {
			signalExportDescriptor = (SignalExportDescriptor) preset;
		}

		SignalSpace space = signalExportDescriptor.getSignalSpace();

		SignalSelection signalSelection = signalView.getSignalSelection(masterPlot);
		Tag tag = null;
		PositionedTag tagSelection = signalView.getTagSelection(masterPlot);
		if (tagSelection != null) {
			if (tagDocument != null && tagSelection.getTagPositionIndex() == signalDocument.getTagDocuments().indexOf(tagDocument)) {
				tag = tagSelection.getTag();
			}
		}

		SignalSpaceConstraints constraints = signalView.createSignalSpaceConstraints();

		space.configureFromSelections(signalSelection, tag);

		if (tagDocument != null) {
			signalExportDescriptor.setTagSet(tagDocument.getTagSet());
			tagDocument.updateSignalSpaceConstraints(constraints);
		} else {
			signalExportDescriptor.setTagSet(null);
			constraints.setMarkerStyles(null);
		}
		signalExportDescriptor.setPageSize(masterPlot.getPageSize());
		signalExportDescriptor.setBlockSize(masterPlot.getBlockSize());

		constraints.setRequireCompletePages(false);

		exportSignalDialog.setConstraints(constraints);

		boolean ok = exportSignalDialog.showDialog(signalExportDescriptor, true);
		if (!ok) {
			return;
		}

		ExportFiles exportFiles = chooseFiles(signalExportDescriptor);
		if (exportFiles == null)
			return;

		SignalSpace signalSpace = signalExportDescriptor.getSignalSpace();

		SignalProcessingChain signalChain;
		try {
			signalChain = masterPlot.getSignalChain().createLevelCopyChain(signalSpace.getSignalSourceLevel());
		} catch (SignalMLException ex) {
			logger.error("Failed to create subchain", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		}

		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();
		MultichannelSampleSource sampleSource = factory.getContinuousSampleSource(signalChain, signalSpace, signalExportDescriptor.getTagSet(), signalExportDescriptor.getPageSize(), signalExportDescriptor.getBlockSize());

		normalizeSamplesIfNeeded(sampleSource, signalExportDescriptor);

		if (saveSignal(sampleSource, exportFiles.getSignalFile(), signalExportDescriptor) == false)
			return;

		if (signalExportDescriptor.isSaveXML())
			if(saveDescriptor(sampleSource, signalExportDescriptor, masterPlot, exportFiles) == false)
				return;

	}

	/**
	 * Shows dialog for choosing files to which the signal and its descriptor
	 * should be exported.
	 * @param signalExportDescriptor the desciptor describing the parameters
	 * of the export
	 * @return true if files were chosen, false if the user cancelled choosing
	 * files
	 */
	protected ExportFiles chooseFiles(SignalExportDescriptor signalExportDescriptor) {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		File fileSuggestion = null;
		if (signalDocument instanceof FileBackedDocument) {
			File originalFile = ((FileBackedDocument) signalDocument).getBackingFile();
			if (originalFile != null) {
				originalFile = new File(originalFile.getName());
				String extension = Util.getFileExtension(originalFile, false);
				if (extension == null || ! "bin".equals(extension)) {
					fileSuggestion = Util.changeOrAddFileExtension(originalFile, "bin");
				}
			}
		}

		File file;
		File xmlFile = null;
		boolean hasFile = false;
		do {
			file = fileChooser.chooseExportSignalFile(optionPaneParent, fileSuggestion);
			if (file == null) {
				return null;
			}
			String ext = Util.getFileExtension(file,false);
			if (ext == null) {
				file = new File(file.getAbsolutePath() + ".bin");
			}

			hasFile = true;

			if (file.exists()) {
				int res = OptionPane.showFileAlreadyExists(optionPaneParent, file.getName());
				if (res != OptionPane.OK_OPTION) {
					hasFile = false;
				}
			}

			if (hasFile && signalExportDescriptor.isSaveXML()) {
				xmlFile = Util.changeOrAddFileExtension(file, "xml");

				if (xmlFile.exists() || xmlFile.equals(file)) {
					int res = OptionPane.showFileAlreadyExists(optionPaneParent, xmlFile.getName());
					if (res != OptionPane.OK_OPTION) {
						hasFile = false;
					}
				}
			}

		} while (!hasFile);

		return new ExportFiles(file, xmlFile);
	}

	/**
	 * Normalizes samples if it is needed or the user chosed an appropriate
	 * option.
	 * @param sampleSource source of samples to be exported
	 * @param signalExportDescriptor the desciptor describing the parameters
	 * of the export
	 */
	protected void normalizeSamplesIfNeeded(MultichannelSampleSource sampleSource, SignalExportDescriptor signalExportDescriptor) {
		RawSignalSampleType sampleType = signalExportDescriptor.getSampleType();
		if (sampleType == RawSignalSampleType.INT || sampleType == RawSignalSampleType.SHORT) {

			// normalization - check signal half-amplitude maximum
			ScanSignalWorker scanWorker = new ScanSignalWorker(sampleSource, pleaseWaitDialog);

			scanWorker.execute();

			pleaseWaitDialog.setActivity(_("scanning signal"));
			pleaseWaitDialog.configureForDeterminate(0, SampleSourceUtils.getMaxSampleCount(sampleSource), 0);
			pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, scanWorker);

			SignalScanResult signalScanResult = null;
			try {
				signalScanResult = scanWorker.get();
			} catch (InterruptedException ex) {
				// ignore
			} catch (ExecutionException ex) {
				logger.error("Worker failed to save", ex.getCause());
				Dialogs.showExceptionDialog((Window) null, ex);
				return;
			}

			double maxSignalAbsValue = Math.max(Math.abs(signalScanResult.getMaxSignalValue()), Math.abs(signalScanResult.getMinSignalValue()));
			double maxTypeAbsValue = 0;

			if (sampleType == RawSignalSampleType.INT) {
				maxTypeAbsValue = Math.min((Integer.MAX_VALUE-1), -(Integer.MIN_VALUE+1));
			} else {
				maxTypeAbsValue = Math.min((Short.MAX_VALUE-1), -(Short.MIN_VALUE+1));
			}

			boolean normalize = signalExportDescriptor.isNormalize();
			if (!normalize) {

				// check if normalization needs to be forced
				if (maxTypeAbsValue < Math.ceil(maxSignalAbsValue)) {

					int ans = OptionPane.showNormalizationUnavoidable(optionPaneParent);
					if (ans != OptionPane.OK_OPTION) {
						return;
					}

					normalize = true;
					signalExportDescriptor.setNormalize(normalize);

				}

			}

			if (normalize) {

				signalExportDescriptor.setNormalizationFactor(maxTypeAbsValue / maxSignalAbsValue);

			}

		}
	}

	/**
	 * Saves the samples to a given file.
	 * @param sampleSource source containing samples to be exported
	 * @param signalFile a file to which the signal should be saved
	 * @param signalExportDescriptor the desciptor describing the parameters
	 * of the export
	 * @return true if succeeded, false otherwise
	 */
	public boolean saveSignal(MultichannelSampleSource sampleSource, File signalFile, SignalExportDescriptor signalExportDescriptor) {
		int minSampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);

		ExportSignalWorker worker = new ExportSignalWorker(sampleSource, signalFile, signalExportDescriptor, pleaseWaitDialog);

		worker.execute();

		pleaseWaitDialog.setActivity(_("exporting signal"));
		pleaseWaitDialog.configureForDeterminate(0, minSampleCount, 0);
		pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

		try {
			worker.get();
		} catch (InterruptedException ex) {
			// ignore
		} catch (ExecutionException ex) {
			logger.error("Worker failed to save", ex.getCause());
			Dialogs.showExceptionDialog((Window) null, ex);
			return false;
		}
		return true;
	}

	/**
	 * Saves the XML descriptor for the signal file.
	 * @param sampleSource source of samples which should be exported
	 * @param signalExportDescriptor the desciptor describing the parameters
	 * of the export
	 * @param masterPlot a {@link SignalPlot} containing the samples to be
	 * exported
	 * @param exportFiles {@link ExportFiles} containing files to which
	 * the signal and its descriptor should be saved
	 * @return true if succeeded, false otherwise
	 */
	public boolean saveDescriptor(MultichannelSampleSource sampleSource, SignalExportDescriptor signalExportDescriptor, SignalPlot masterPlot, ExportFiles exportFiles) {
			if (descriptorWriter == null) {
				descriptorWriter = new RawSignalDescriptorWriter();
			}

			SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
			SignalSpace signalSpace = signalExportDescriptor.getSignalSpace();
			RawSignalDescriptor rawDescriptor = new RawSignalDescriptor();

			float samplingFrequency = sampleSource.getSamplingFrequency();

			rawDescriptor.setBlocksPerPage(masterPlot.getBlocksPerPage());
			rawDescriptor.setByteOrder(signalExportDescriptor.getByteOrder());
			int channelCount = sampleSource.getChannelCount();
			rawDescriptor.setChannelCount(channelCount);
			rawDescriptor.setEegSystemName(signalDocument.getMontage().getEegSystemName());

			if (signalExportDescriptor.isNormalize()) {
				rawDescriptor.setCalibrationGain((float)(1 / signalExportDescriptor.getNormalizationFactor()));
			} else {
				rawDescriptor.setCalibrationGain(1F);
			}
			rawDescriptor.setCalibrationOffset(0);

			String[] labels = new String[channelCount];
			for (int i=0; i<channelCount; i++) {
				labels[i] = sampleSource.getLabel(i);
			}
			rawDescriptor.setChannelLabels(labels);
			rawDescriptor.setExportDate(new Date());
			rawDescriptor.setExportFileName(exportFiles.getSignalFile().getName());

			TimeSpaceType timeSpaceType = signalSpace.getTimeSpaceType();
			if (timeSpaceType == TimeSpaceType.MARKER_BASED) {

				MarkerTimeSpace markerTimeSpace = signalSpace.getMarkerTimeSpace();

				rawDescriptor.setMarkerOffset(markerTimeSpace.getSecondsBefore());
				rawDescriptor.setPageSize((float)(markerTimeSpace.getSecondsBefore() + markerTimeSpace.getSecondsAfter()));

			} else {

				rawDescriptor.setMarkerOffset(0);
				rawDescriptor.setPageSize(masterPlot.getPageSize());

			}

			int minSampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
			rawDescriptor.setSampleCount(minSampleCount);
			rawDescriptor.setSampleType(signalExportDescriptor.getSampleType());
			rawDescriptor.setSamplingFrequency(samplingFrequency);
			if (signalDocument instanceof FileBackedDocument) {
				File sourceFile = ((FileBackedDocument) signalDocument).getBackingFile();
				if (sourceFile != null) {
					rawDescriptor.setSourceFileName(sourceFile.getName());
				}
			}

			if (signalDocument instanceof SignalMLDocument) {

				SignalMLDocument signalMLDocument = (SignalMLDocument) signalDocument;

				rawDescriptor.setSourceSignalType(SourceSignalType.SIGNALML);
				rawDescriptor.setSourceSignalMLFormat(signalMLDocument.getFormatName());
				rawDescriptor.setSourceSignalMLSourceUID(signalMLDocument.getSourceUID());

			} else {

				rawDescriptor.setSourceSignalType(SourceSignalType.RAW);

			}

			try {
				descriptorWriter.writeDocument(rawDescriptor, exportFiles.getXmlFile());
			} catch (IOException ex) {
				logger.error("Worker failed to save xml", ex);
				Dialogs.showExceptionDialog((Window) null, ex);
				return false;
			}
			return true;

	}

	@Override
	public void setEnabledAsNeeded() {
		SignalDocumentFocusSelector x = getActionFocusSelector();
		if (null != x)
			setEnabled(x.getActiveSignalDocument() != null);
	}

	public ExportSignalDialog getExportSignalDialog() {
		return exportSignalDialog;
	}

	public void setExportSignalDialog(ExportSignalDialog exportSignalDialog) {
		this.exportSignalDialog = exportSignalDialog;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

	/**
	 * This class holds the files to which the signal and its descriptor
	 * will be saved.
	 */
	protected class ExportFiles {

		/**
		 * The file to which the signal will be exported.
		 */
		private File signalFile;

		/**
		 * The file to which the exported signal XML descriptor will be written.
		 */
		private File xmlFile;

		/**
		 * Constructor.
		 * @param signalFile a file to which the signal will be saved
		 * @param xmlFile a file to which the signal descriptor will be saved
		 */
		public ExportFiles(File signalFile, File xmlFile) {
			this.signalFile = signalFile;
			this.xmlFile = xmlFile;
		}

		/**
		 * Returns a file to which the signal should be exported.
		 * @return a file to which the signal should be exported
		 */
		public File getSignalFile() {
			return signalFile;
		}

		/**
		 * Returns a file to which the signal's descriptor should be written.
		 * @return a file to which the signal's descriptor should be written;
		 */
		public File getXmlFile() {
			return xmlFile;
		}

	}

}
