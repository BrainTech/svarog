/* RawSignalDocument.java created 2008-01-28
 *
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;

/** RawSignalDocument
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalDocument extends AbstractFileSignal {

	private RawSignalDescriptor descriptor;

	public RawSignalDocument(SignalType type, RawSignalDescriptor descriptor) {
		super(type);
		this.descriptor = descriptor;
	}

	@Override
	public void closeDocument() throws SignalMLException {
		if (sampleSource != null) {
			((RawSignalSampleSource) sampleSource).close();
			sampleSource = null;
		}
		super.closeDocument();
	}

	@Override
	public void openDocument() throws SignalMLException, IOException {
		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}
		RawSignalSampleSource sampleSource = new RawSignalSampleSource(backingFile.getAbsoluteFile(), descriptor.getChannelCount(), descriptor.getSamplingFrequency(), descriptor.getSampleType(), descriptor.getByteOrder());
		sampleSource.setCalibration(descriptor.getCalibration());
		sampleSource.setLabels(descriptor.getChannelLabels());

		this.sampleSource = sampleSource;
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

	public void setSamplingFrequency(float samplingFrequency) {
		sampleSource.setSamplingFrequency(samplingFrequency);
	}

	public String[] getLabels() {
		return descriptor.getChannelLabels();
	}

	public RawSignalSampleType getSampleType() {
		return descriptor.getSampleType();
	}

	public RawSignalByteOrder getByteOrder() {
		return descriptor.getByteOrder();
	}

	public RawSignalDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public String getFormatName() {
		return "InternalInterleavedRAW";
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor("property.signaldocument.calibration", "calibration", RawSignalDocument.class));
		list.add(new LabelledPropertyDescriptor("property.rawSignal.sampleType", "sampleType", RawSignalDocument.class, "getSampleType", null));
		list.add(new LabelledPropertyDescriptor("property.rawSignal.byteOrder", "byteOrder", RawSignalDocument.class, "getByteOrder", null));

		return list;

	}

}

