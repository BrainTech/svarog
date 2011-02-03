/* ExportSignalAction.java created 2008-01-27
 *
 */
package org.signalml.app.action;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.ExportSignalDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalScanResult;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.ExportSignalWorker;
import org.signalml.app.worker.ScanSignalWorker;
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
import org.springframework.context.support.MessageSourceAccessor;

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

	public ExportSignalAction(MessageSourceAccessor messageSource, SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(messageSource, signalDocumentFocusSelector);
		setText("action.exportSignal");
		setToolTip("action.exportSignalToolTip");
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
				return;
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

		SignalSpace signalSpace = signalExportDescriptor.getSignalSpace();

		SignalProcessingChain signalChain;
		try {
			signalChain = masterPlot.getSignalChain().createLevelCopyChain(signalSpace.getSignalSourceLevel());
		} catch (SignalMLException ex) {
			logger.error("Failed to create subchain", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}

		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();
		MultichannelSampleSource sampleSource = factory.getContinuousSampleSource(signalChain, signalSpace, signalExportDescriptor.getTagSet(), signalExportDescriptor.getPageSize(), signalExportDescriptor.getBlockSize());

		RawSignalSampleType sampleType = signalExportDescriptor.getSampleType();
		if (sampleType == RawSignalSampleType.INT || sampleType == RawSignalSampleType.SHORT) {

			// normalization - check signal half-amplitude maximum
			ScanSignalWorker scanWorker = new ScanSignalWorker(sampleSource, pleaseWaitDialog);

			scanWorker.execute();

			pleaseWaitDialog.setActivity(messageSource.getMessage("activity.scanningSignal"));
			pleaseWaitDialog.configureForDeterminate(0, SampleSourceUtils.getMaxSampleCount(sampleSource), 0);
			pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, scanWorker);

			SignalScanResult signalScanResult = null;
			try {
				signalScanResult = scanWorker.get();
			} catch (InterruptedException ex) {
				// ignore
			} catch (ExecutionException ex) {
				logger.error("Worker failed to save", ex.getCause());
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
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

		int minSampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);

		ExportSignalWorker worker = new ExportSignalWorker(sampleSource, file, signalExportDescriptor, pleaseWaitDialog);

		worker.execute();

		pleaseWaitDialog.setActivity(messageSource.getMessage("activity.exportingSignal"));
		pleaseWaitDialog.configureForDeterminate(0, minSampleCount, 0);
		pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);

		try {
			worker.get();
		} catch (InterruptedException ex) {
			// ignore
		} catch (ExecutionException ex) {
			logger.error("Worker failed to save", ex.getCause());
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
			return;
		}

		if (signalExportDescriptor.isSaveXML()) {

			if (descriptorWriter == null) {
				descriptorWriter = new RawSignalDescriptorWriter();
			}

			RawSignalDescriptor rawDescriptor = new RawSignalDescriptor();

			float samplingFrequency = sampleSource.getSamplingFrequency();

			rawDescriptor.setBlocksPerPage(masterPlot.getBlocksPerPage());
			rawDescriptor.setByteOrder(signalExportDescriptor.getByteOrder());
			int channelCount = sampleSource.getChannelCount();
			rawDescriptor.setChannelCount(channelCount);

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
			rawDescriptor.setExportFileName(file.getName());

			TimeSpaceType timeSpaceType = signalSpace.getTimeSpaceType();
			if (timeSpaceType == TimeSpaceType.MARKER_BASED) {

				MarkerTimeSpace markerTimeSpace = signalSpace.getMarkerTimeSpace();

				rawDescriptor.setMarkerOffset(markerTimeSpace.getSecondsBefore());
				rawDescriptor.setPageSize((float)(markerTimeSpace.getSecondsBefore() + markerTimeSpace.getSecondsAfter()));

			} else {

				rawDescriptor.setMarkerOffset(0);
				rawDescriptor.setPageSize(masterPlot.getPageSize());

			}

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
				descriptorWriter.writeDocument(rawDescriptor, xmlFile);
			} catch (IOException ex) {
				logger.error("Worker failed to save xml", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}

		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
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

}
