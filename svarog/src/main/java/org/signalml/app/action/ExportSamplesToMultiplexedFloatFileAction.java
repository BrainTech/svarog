/* ExportSamplesToMultiplexedFloatFileAction.java created 2008-01-15
 *
 */

package org.signalml.app.action;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;

/** ExportSamplesToMultiplexedFloatFileAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportSamplesToMultiplexedFloatFileAction extends AbstractSignalMLAction {

	protected static final Logger logger = Logger.getLogger(ExportSamplesToMultiplexedFloatFileAction.class);

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	private RawSignalWriter rawSignalWriter;

	public ExportSamplesToMultiplexedFloatFileAction() {
		super();
		setText(_("Save all samples as float file"));
		setIconPath("org/signalml/app/icon/filesave.png");
		setToolTip(_("Save all samples as a float file"));
	}

	protected abstract int getSampleCount();

	protected abstract double[][] getSamples();

	@Override
	public void actionPerformed(ActionEvent ev) {

		double[][] samples = getSamples();

		if (samples != null) {

			int channelCount = samples.length;
			int sampleCount = getSampleCount();

			File file;
			boolean hasFile = false;
			do {

				file = fileChooser.chooseSamplesSaveAsFloatFile(optionPaneParent);
				if (file == null) {
					return;
				}
				String ext = Util.getFileExtension(file,false);
				if (ext == null) {
					file = new File(file.getAbsolutePath() + ".bin");
				}

				hasFile = true;

				if (file.exists()) {
					int res = OptionPane.showFileAlreadyExists(optionPaneParent);
					if (res != OptionPane.OK_OPTION) {
						hasFile = false;
					}
				}

			} while (!hasFile);

			if (rawSignalWriter == null) {
				rawSignalWriter = new RawSignalWriter();
			}

			DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(samples, channelCount, sampleCount);

			SignalExportDescriptor descriptor = new SignalExportDescriptor();

			descriptor.setSampleType(RawSignalSampleType.FLOAT);
			descriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
			descriptor.setNormalize(false);

			try {
				rawSignalWriter.writeSignal(file, sampleSource, descriptor, null);
			} catch (IOException ex) {
				logger.error("Failed to save to file - i/o exception", ex);
				Dialogs.showExceptionDialog((Window) null, ex);
				return;
			}

		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
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
