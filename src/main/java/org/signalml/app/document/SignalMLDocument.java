/* SignalMLDocument.java created 2007-09-18
 *
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.domain.signal.SignalMLCodecSampleSource;
import org.signalml.domain.signal.SignalType;
import org.signalml.exception.SignalMLException;

/** SignalMLDocument
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLDocument extends AbstractFileSignal {

	private SignalMLCodecReader reader;

	public SignalMLDocument(SignalMLCodecReader reader, SignalType type) {
		super(type);
		this.reader = reader;
	}

	public SignalMLCodecReader getReader() {
		return reader;
	}

	@Override
	public void closeDocument() throws SignalMLException {
		reader.close();
		super.closeDocument();
	}

	@Override
	public void openDocument() throws SignalMLException, IOException {
		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}
		reader.open(backingFile.getAbsolutePath());
		sampleSource = new SignalMLCodecSampleSource(reader);
	}

	public float getCalibration() {
		return sampleSource.getCalibration();
	}

	public boolean isCalibrationCapable() {
		return sampleSource.isCalibrationCapable();
	}

	public boolean isChannelCountCapable() {
		return sampleSource.isChannelCountCapable();
	}

	public boolean isSamplingFrequencyCapable() {
		return sampleSource.isSamplingFrequencyCapable();
	}

	public void setCalibration(float calibration) {
		sampleSource.setCalibration(calibration);
	}

	public void setChannelCount(int channelCount) {
		sampleSource.setChannelCount(channelCount);
	}

	public void setSamplingFrequency(float samplingFrequency) {
		sampleSource.setSamplingFrequency(samplingFrequency);
	}

	@Override
	public String getFormatName() {
		return reader.getCodec().getFormatName();
	}

	public String getSourceUID() {
		return reader.getCodec().getSourceUID();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor("property.signalmldocument.formatName", "formatName", SignalMLDocument.class, "getFormatName", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.calibrationCapable", "caligrationCapable", SignalMLDocument.class, "isCalibrationCapable", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.calibration", "calibration", SignalMLDocument.class));

		return list;

	}

}

