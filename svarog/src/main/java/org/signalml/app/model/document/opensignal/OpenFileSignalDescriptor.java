/* OpenFileSignalDescriptor.java created 2007-09-18
 *
 */

package org.signalml.app.model.document.opensignal;

import java.io.File;

import org.signalml.app.model.signal.SignalParameterDescriptor;
import org.signalml.app.view.document.opensignal.FileOpenSignalMethod;
import org.signalml.codec.SignalMLCodec;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;

/**
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenFileSignalDescriptor {

	/**
	 * The file containing the signal.
	 */
	private File file;

	/**
	 * The method used to open the file (RAW or using a SignalML codec).
	 */
	private FileOpenSignalMethod method;

	// for signalML signals
	private SignalMLCodec codec;
	private SignalParameterDescriptor parameters = new SignalParameterDescriptor();

	// for raw signals
	private RawSignalDescriptor rawSignalDescriptor = new RawSignalDescriptor();

	public OpenFileSignalDescriptor() {
		method = FileOpenSignalMethod.RAW;

		rawSignalDescriptor.setSamplingFrequency(128F);
		rawSignalDescriptor.setChannelCount(2);
		rawSignalDescriptor.setSampleType(RawSignalSampleType.DOUBLE);
		rawSignalDescriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
		rawSignalDescriptor.setCalibrationGain(1F);
		rawSignalDescriptor.setCalibrationOffset(0.0F);
		rawSignalDescriptor.setPageSize(20.0F);
		rawSignalDescriptor.setBlocksPerPage(5);

	}

	public FileOpenSignalMethod getMethod() {
		return method;
	}

	public void setMethod(FileOpenSignalMethod method) {
		this.method = method;
	}

	public SignalMLCodec getCodec() {
		return codec;
	}

	public void setCodec(SignalMLCodec codec) {
		this.codec = codec;
	}

	public SignalParameterDescriptor getParameters() {
		return parameters;
	}

	public void setParameters(SignalParameterDescriptor parameters) {
		this.parameters = parameters;
	}

	public RawSignalDescriptor getRawSignalDescriptor() {
		return rawSignalDescriptor;
	}

	public void setRawSignalDescriptor(RawSignalDescriptor rawSignalDescriptor) {
		this.rawSignalDescriptor = rawSignalDescriptor;
	}

	/**
	 * Returns the file to be opened.
	 * @return the file to be opened
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the file to be opened.
	 * @param file the file to be opened
	 */
	public void setFile(File file) {
		this.file = file;
	}

}
