package org.signalml.app.document.signal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import static org.signalml.domain.signal.ascii.AsciiSignalDescriptorReader.FIRST_LINE_START;
import static org.signalml.domain.signal.ascii.AsciiSignalDescriptorReader.SEPARATOR_REGEX;

/**
 * Sample source for ASCII files.
 * This class is responsible for quasi-random file access,
 * by storing the positions of end-line markers.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class AsciiSignalSampleSource extends BaseSignalSampleSource {

	protected static final Logger logger = Logger.getLogger(AsciiSignalSampleSource.class);

	private final static int INTERVAL_BETWEEN_STORED_OFFSETS = 10;

	private final IndexedTextFile index;
	private final boolean startsWithComment;
	private boolean parsingErrorDialogShown;

	public AsciiSignalSampleSource(File file, int channelCount, float samplingFrequency) throws IOException {
		super(file, channelCount, samplingFrequency);
		index = new IndexedTextFile(file, INTERVAL_BETWEEN_STORED_OFFSETS);
		int lineCount = index.getLineCount();
		startsWithComment = lineCount > 0 && index.getLine(0).startsWith(Character.toString(FIRST_LINE_START));
		sampleCount = startsWithComment ? lineCount - 1 : lineCount;
	}

	@Override
	public void close() {
		index.close();
	}

	@Override
	protected BaseSignalSampleSource duplicateInternal() throws IOException {
		return new AsciiSignalSampleSource(getFile(), getChannelCount(), getSamplingFrequency());
	}

	@Override
	public synchronized void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		int channelCount = getChannelCount();
		if (channel < 0 || channel >= channelCount) {
			throw new IndexOutOfBoundsException("Bad channel number [" + channel + "]");
		}
		if ((signalOffset < 0) || ((signalOffset + count) > sampleCount)) {
			throw new IndexOutOfBoundsException("Signal range [" + signalOffset + ":" + count + "] doesn't fit in the signal");
		}
		if ((arrayOffset < 0) || ((arrayOffset + count) > target.length)) {
			throw new IndexOutOfBoundsException("Target range [" + arrayOffset + ":" + count + "] doesn't fit in the target array");
		}

		int lineOffset = signalOffset;
		if (startsWithComment) {
			++lineOffset;
		}
		boolean csvParsingErrors = false;
		try {
			for (int i=0; i<count; ++i) {
				String line = index.getLine(lineOffset + i);
				String[] values = line.split(SEPARATOR_REGEX);
				if (values.length != channelCount) {
					throw new IOException("invalid data format");
				}
				try {
					double sample = Double.parseDouble(values[channel]);
					target[arrayOffset + i] = performCalibration(channel, sample);
				} catch (NumberFormatException ex) {
					csvParsingErrors = true;
					target[arrayOffset + i] = 0.0;
				}
			}
		} catch (IOException ex) {
			logger.warn("cannot read from CSV file", ex);
			Arrays.fill(target, arrayOffset, arrayOffset+count, 0.0);
		}
		if (csvParsingErrors && !parsingErrorDialogShown) {
			// display the error dialog only once per file
			parsingErrorDialogShown = true;
			Dialogs.showError(_("CSV file is badly formatted. Some data may not be displayed properly."));
		}
	}

}
