package org.signalml.codec;

import static org.signalml.util.Util.toMD5String;

import org.apache.log4j.Logger;

public class JsignalmlCodec implements SignalMLCodec {
	protected static final Logger log = Logger.getLogger(SignalMLCodec.class);

	private jsignalml.compiler.CompiledClass<? extends jsignalml.Source> klass;

	public JsignalmlCodec(jsignalml.compiler.CompiledClass<? extends jsignalml.Source> klass) {
		this.klass = klass;
	}

	public String getSourceUID() {
		return toMD5String(klass.src);
	}

	private String formatName = null;
	public String getFormatName() {
		if (formatName == null)
			try {
				formatName = klass.newInstance().getFormatName();
			} catch(Exception e) {
				log.error("failed to create instance", e);
				formatName = klass.fullName;
			}
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public SignalMLCodecReader createReader() throws SignalMLCodecException {
		final jsignalml.Source source;
		try {
			source = klass.newInstance();
		} catch(Exception e) {
			log.error("failed to create instance", e);
			throw new SignalMLCodecException(e);
		}
		return new JsignalmlReader(source, this);
	}

	public String toString() {
		return getClass().getSimpleName() + " for " + getFormatName();
	}
}
