package org.signalml.app.method.ep.action;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.signalml.app.document.TagDocument;
import org.signalml.app.method.ep.SelectTagGroupDialog;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.Util;

/**
 * An action for saving evoked potentials to a a float file.
 *
 * @author Piotr Szachewicz
 */
public class ExportAllEPSamplesToFloatFileAction extends AbstractSaveAction {

	public ExportAllEPSamplesToFloatFileAction(ViewerFileChooser fileChooser) {
		super(fileChooser);
		setText(_("Save samples to file"));
		setToolTip(_("Save samples to file"));
		setIconPath("org/signalml/app/icon/filesave.png");
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		List<TagStyleGroup> averagedTagStyles = result.getData().getParameters().getAveragedTagStyles();
		TagStyleGroup selectedGroup = averagedTagStyles.get(0);
		if (averagedTagStyles.size() > 1)
			selectedGroup = showTagStyleGroupSelection();

		if (selectedGroup == null)
			return;

		//selected index
		int i;
		for (i = 0; i < averagedTagStyles.size(); i++) {
			if (averagedTagStyles.get(i).equals(selectedGroup))
				break;
		}

		// file selection
		File file = showFileChooserDialog();
		if (file == null)
			return;

		try {
			writeData(file, i);
		} catch (Exception e) {
			Dialogs.showExceptionDialog(e);
			logger.error("", e);
			return;
		}

	}

	protected TagStyleGroup showTagStyleGroupSelection() {
		SelectTagGroupDialog dialog = new SelectTagGroupDialog();
		List<TagStyleGroup> averagedTagStyles = result.getData().getParameters().getAveragedTagStyles();
		List<TagStyleGroup> selectedGroups = new ArrayList<TagStyleGroup>();
		selectedGroups.addAll(averagedTagStyles);

		boolean okPressed = dialog.showDialog(selectedGroups);

		if (!okPressed)
			return null;

		return selectedGroups.get(0);
	}

	protected File showFileChooserDialog() {
		File file;
		boolean hasFile = false;
		do {
			file = fileChooser.chooseSamplesSaveAsTextFile(null);
			if (file == null) {
				return null;
			}
			String ext = Util.getFileExtension(file, false);
			if (ext == null) {
				file = new File(file.getAbsolutePath() + ".bin");
			}

			hasFile = true;

			if (file.exists()) {
				int res = OptionPane.showFileAlreadyExists(null);
				if (res != OptionPane.OK_OPTION) {
					hasFile = false;
				}
			}

		} while (!hasFile);

		return file;
	}

	protected void writeData(File file, int groupIndex) throws IOException, SignalMLException {
		double[][] samples = result.getAverageSamples().get(groupIndex);
		int channelCount = samples.length;
		int sampleCount = samples[0].length;
		DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(samples, channelCount, sampleCount);

		//signal
		RawSignalWriter rawSignalWriter = new RawSignalWriter();
		SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
		rawSignalWriter.writeSignal(file, sampleSource, signalExportDescriptor, null);

		//descriptor
		RawSignalDescriptorWriter descriptorWriter = new RawSignalDescriptorWriter();
		RawSignalDescriptor descriptor = new RawSignalDescriptor();
		descriptor.setSampleCount(sampleCount);
		descriptor.setChannelCount(channelCount);
		descriptor.setExportFileName(file.getName());
		descriptor.setChannelLabels(result.getLabels());

		File xmlFile = Util.changeOrAddFileExtension(file, "xml");
		descriptorWriter.writeDocument(descriptor, xmlFile);

		//tag file
		File tagFile = Util.changeOrAddFileExtension(file, "tag");
		TagDocument tagDocument = new TagDocument();
		tagDocument.setBackingFile(tagFile);
		StyledTagSet tagSet = tagDocument.getTagSet();
		TagStyle eventTagStyle = new TagStyle(SignalSelectionType.CHANNEL, "event", "", Color.red, Color.red, 0);
		eventTagStyle.setMarker(true);
		tagSet.addStyle(eventTagStyle);
		double eventPosition = -1 * result.getData().getParameters().getAveragingStartTime();
		tagSet.addTag(new Tag(eventTagStyle, eventPosition, 0.0));

		tagDocument.saveDocument();
		tagDocument.closeDocument();

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(result != null);
	}

}