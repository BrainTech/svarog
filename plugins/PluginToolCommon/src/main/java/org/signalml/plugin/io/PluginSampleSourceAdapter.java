package org.signalml.plugin.io;

import java.beans.PropertyChangeListener;
import java.io.InvalidClassException;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.OriginalMultichannelSampleSource;
import org.signalml.domain.signal.samplesource.SignalMLCodecSampleSource;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.signal.ChannelSamples;

public class PluginSampleSourceAdapter implements MultichannelSampleSource {

	private SvarogAccessSignal signalAccess;
	private ExportedSignalDocument signalDocument;

	private MultichannelSampleSource delegate;

	public PluginSampleSourceAdapter(SvarogAccessSignal signalAccess,
									 ExportedSignalDocument signalDocument) {
		this.signalAccess = signalAccess;
		this.signalDocument = signalDocument;
		this.delegate = null;

		if (this.signalDocument instanceof SignalDocument) {
			OriginalMultichannelSampleSource source = ((SignalDocument) this.signalDocument).getSampleSource();

			try {
				SignalMLCodecSampleSource codecSource = (SignalMLCodecSampleSource) source;
				this.delegate = new FastMultichannelSampleSource(
					codecSource.getReader());
			} catch (ClassCastException e) {
				// do nothing
			}

		}
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return this.delegate != null ? this.delegate
			   .isSamplingFrequencyCapable() : true;
	}

	@Override
	public boolean isChannelCountCapable() {
		return this.delegate != null ? this.delegate.isChannelCountCapable()
			   : true;
	}

	@Override
	public float getSamplingFrequency() {
		return this.delegate == null ? this.signalDocument
			   .getSamplingFrequency() : this.delegate.getSamplingFrequency();
	}

	@Override
	public int getChannelCount() {
		return this.delegate == null ? this.signalDocument.getChannelCount()
			   : this.delegate.getChannelCount();
	}

	@Override
	public int getSampleCount(int channel) {
		return this.delegate == null ? (int)(this.signalDocument
											 .getMaxSignalLength() * this.signalDocument
											 .getSamplingFrequency()) : this.delegate
			   .getSampleCount(channel);
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset,
						   int count, int arrayOffset) {
		if (this.delegate == null) {
			ChannelSamples samples;
			try {
				samples = this.signalAccess.getRawSignalSamplesFromDocument(
							  this.signalDocument, channel, signalOffset, count);
			} catch (InvalidClassException e) {
				e.printStackTrace();
				return;
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return;
			}
			System.arraycopy(samples.getSamples(), 0, target, arrayOffset,
							 count);
		} else {
			this.delegate.getSamples(channel, target, signalOffset, count,
									 arrayOffset);
		}
	}

	@Override
	public String getLabel(int channel) {
		return this.signalDocument.getSourceChannelLabels().get(channel);
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return this.delegate == null ? -1 : this.delegate
			   .getDocumentChannelIndex(channel);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.signalDocument.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.signalDocument.removePropertyChangeListener(listener);
	}

	@Override
	public void destroy() {
		if (this.delegate != null) {
			this.delegate.destroy();
		}
	}
	

}
