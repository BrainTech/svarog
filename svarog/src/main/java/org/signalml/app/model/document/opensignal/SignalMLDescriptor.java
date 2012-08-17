package org.signalml.app.model.document.opensignal;

import org.signalml.app.document.signal.SignalMLDocument;
import org.signalml.codec.SignalMLCodec;

/**
 * Describes the parameters of a SignalML file to be opened.
 *
 * @author Piotr Szachewicz
 */
public class SignalMLDescriptor extends AbstractOpenSignalDescriptor {

	/**
	 * The codec that will be used to open the signalML file.
	 */
	private transient SignalMLCodec codec;

	private String formatName;
	private String codecUID;

	public SignalMLDescriptor() {
	}

	public SignalMLDescriptor(SignalMLDocument signalMLDocument) {
		int channelCount = signalMLDocument.getChannelCount();
		float samplingFrequency = signalMLDocument.getSamplingFrequency();

		String[] channelLabels = new String[channelCount];
		for (int i = 0; i < channelCount; i++) {
			channelLabels[i] = signalMLDocument.getSampleSource().getLabel(i);
		}

		setChannelLabels(channelLabels);
		signalParameters.setChannelCount(channelCount);
		signalParameters.setSamplingFrequency(samplingFrequency);

		setMontage(signalMLDocument.getMontage());
		setCodec(signalMLDocument.getReader().getCodec());

		// TODO gain/offset
	}

	public SignalMLCodec getCodec() {
		return codec;
	}

	public void setCodec(SignalMLCodec codec) {
		this.codec = codec;
		if (codec == null) {
			formatName = "";
			codecUID = "";
		} else {
			formatName = codec.getFormatName();
			codecUID = codec.getSourceUID();
		}
	}

	public String getFormatName() {
		return formatName;
	}

	public String getCodecUID() {
		return codecUID;
	}

}
