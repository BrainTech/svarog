package org.signalml.codec;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

public class StaticCodec implements SignalMLCodec {

	protected static final Logger log = Logger.getLogger(StaticCodec.class);

	private String formatName;
	private final Class<? extends jsignalml.Source> klass;

	public StaticCodec(Class<? extends jsignalml.Source> klass) {
		this.klass = klass;
		this.formatName = klass.getSimpleName();
		log.debug("new static codec " + this.formatName +
				  " from " + klass.getCanonicalName());
	}

	public String getFormatName() {
		return this.formatName;
	}

	public void setFormatName(String formatName) {
		assert formatName != null;
		this.formatName = formatName;
	}

	@Override
	public String toString() {
		return formatName;
	}

	public String getSourceUID() {
		return this.klass.getCanonicalName();
	}

	@Override
	public SignalMLCodecReader createReader() throws SignalMLCodecException {

		final jsignalml.Source source;
		try {
			Constructor ctor = this.klass.getDeclaredConstructor();
			source = (jsignalml.Source) ctor.newInstance();
		} catch (Exception e) {
			log.error("Failed to initialize codec instance", e);
			throw new SignalMLCodecException(e);
		}

		return new JsignalmlReader(source, this);
	}
}
