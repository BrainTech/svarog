/* AbstractSampleProcessor.java created 2007-09-24
 * 
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** AbstractSampleProcessor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class MultichannelSampleProcessor extends AbstractMultichannelSampleSource implements MultichannelSampleSource, PropertyChangeListener {

	protected MultichannelSampleSource source;
		
	public MultichannelSampleProcessor(MultichannelSampleSource source) {
		super();
		this.source = source;
		source.addPropertyChangeListener(this);
	}
	
	@Override
	public void destroy() {
		source.removePropertyChangeListener(this);
	}
	
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

	@Override
	public boolean isCalibrationCapable() {
		return source.isCalibrationCapable();
	}
	
	@Override
	public boolean isChannelCountCapable() {
		return source.isChannelCountCapable();
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return source.isSamplingFrequencyCapable();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		pcSupport.firePropertyChange(evt);
	}
		
}
