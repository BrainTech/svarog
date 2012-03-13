package org.signalml.app.model.document.opensignal;

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
	private SignalMLCodec codec;

	public SignalMLCodec getCodec() {
		return codec;
	}

	public void setCodec(SignalMLCodec codec) {
		this.codec = codec;
	}
}
