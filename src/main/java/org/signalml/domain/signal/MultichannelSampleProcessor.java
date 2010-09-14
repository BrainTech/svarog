/* AbstractSampleProcessor.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.signalml.domain.signal.space.ChannelSubsetSampleSource;
import org.signalml.domain.signal.space.MarkerSegmentedSampleSource;
import org.signalml.domain.signal.space.SelectionSegmentedSampleSource;

/**
 * This class represents an abstract processor of source samples for a
 * multichannel signal.
 * It is also a listener for property changes.
 *
 * @see AbstractMultichannelSampleSource
 * @see ChannelSubsetSampleSource
 * @see MarkerSegmentedSampleSource
 * @see MultichannelSampleBuffer
 * @see MultichannelSampleMontage
 * @see SelectionSegmentedSampleSource
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class MultichannelSampleProcessor extends AbstractMultichannelSampleSource implements MultichannelSampleSource, PropertyChangeListener {

        /**
         * the actual {@link MultichannelSampleSource source} of samples for
         * a multichannel signal
         */
	protected MultichannelSampleSource source;

        /**
         * Constructor. Creates a processor using a given
         * {@link MultichannelSampleSource source}
         * @param source the actual source of signal samples
         */
	public MultichannelSampleProcessor(MultichannelSampleSource source) {
		super();
		this.source = source;
		source.addPropertyChangeListener(this);
	}

	@Override
	public void destroy() {
		source.removePropertyChangeListener(this);
	}

        /**
         * Returns the actual {@link MultichannelSampleSource source} of samples
         * @return  the actual source of samples
         */
	public MultichannelSampleSource getSource() {
		return source;
	}

	@Override
	public float getCalibration() {
		return source.getCalibration();
	}

	@Override
	public int getChannelCount() {
		return source.getChannelCount();
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return source.getDocumentChannelIndex(channel);
	}

	@Override
	public String getLabel(int channel) {
		return source.getLabel(channel);
	}

	@Override
	public int getSampleCount(int channel) {
		return source.getSampleCount(channel);
	}

	@Override
	public float getSamplingFrequency() {
		return source.getSamplingFrequency();
	}

        /**
         * Returns if the actual {@link MultichannelSampleSource sample source}
         * is capable of returning a calibration
         * @return true if the actual sample source is capable of
         * returning a calibration, false otherwise
         */
	@Override
	public boolean isCalibrationCapable() {
		return source.isCalibrationCapable();
	}

        /**
         * Returns if the actual {@link MultichannelSampleSource sample source}
         * is capable of returning a channel count
         * @return true if the actual sample source is capable of
         * returning a channel count, false otherwise
         */
	@Override
	public boolean isChannelCountCapable() {
		return source.isChannelCountCapable();
	}

        /**
         * Returns if the actual {@link MultichannelSampleSource sample source}
         * is capable of returning a sampling frequency
         * @return true if the actual sample source is capable of
         * returning a sampling frequency, false otherwise
         */
	@Override
	public boolean isSamplingFrequencyCapable() {
		return source.isSamplingFrequencyCapable();
	}

        /**
         * Fires all listeners in this listener that the property has changed
         * @param evt an event describing the change
         */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		pcSupport.firePropertyChange(evt);
	}

}
