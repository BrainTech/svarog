/* SignalMLCodecSampleSource.java created 2007-09-24
 * 
 */

package org.signalml.domain.signal;

import org.apache.log4j.Logger;
import org.signalml.codec.SignalMLCodecException;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;

/** SignalMLCodecSampleSource
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLCodecSampleSource extends AbstractMultichannelSampleSource implements OriginalMultichannelSampleSource, ResamplableSampleSource {

	protected static final Logger logger = Logger.getLogger(SignalMLCodecSampleSource.class);
		
	private SignalMLCodecReader reader = null;
	private float samplingFrequency;
	private int channelCount;
	private float calibration;
	private int sampleCount;

	private boolean samplingFrequencyCapable = false;
	private boolean channelCountCapable = false;
	private boolean calibrationCapable = false;
	
	private boolean uniformSampling = true;
	private float[] channelSampling;	
	
	private String[] labels;
	
	private boolean samplingFrequencyExternal = false;
	private boolean channelCountExternal = false;
	private boolean calibrationExternal = false;
	
	private MultichannelSignalResampler resampler = null;
	
	public SignalMLCodecSampleSource(SignalMLCodecReader reader) throws SignalMLException {
		super();
		this.reader = reader;

		try {
			sampleCount = reader.get_max_offset() + 1;
		} catch (SignalMLCodecException ex) {
			logger.error("Codec doesn't support max offset - unusable");
			throw ex;
		}
				
		try {
			channelCountCapable = reader.is_number_of_channels();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_number_of_channels, left false");
			logger.debug("Caught exception was", e);
			channelCountCapable = false;
		}
		
		if( channelCountCapable ) {
			try {
				channelCount = reader.get_number_of_channels();
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec doesn't support channel count, assumed 1");
				logger.debug("Caught exception was", e);
				channelCount = 1;
				channelCountCapable = false;
			}
		}
		
		try {
			samplingFrequencyCapable = reader.is_sampling_frequency();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_sampling_frequency, left false");
			logger.debug("Caught exception was", e);
		}
		
		if( samplingFrequencyCapable ) {
			try {
				samplingFrequency = reader.get_sampling_frequency();			
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec doesn't support sampling frequency, left null");
				logger.debug("Caught exception was", e);
				samplingFrequencyCapable = false;
			}
			
			if( samplingFrequencyCapable ) {
				
				try {
					uniformSampling = reader.is_uniform_sampling_frequency();			
				} catch (SignalMLCodecException e) {
					logger.warn("WARNING: codec doesn't support uniform sampling frequency info, left true");
					logger.debug("Caught exception was", e);
				}
				
				if( !uniformSampling ) {
					
					logger.warn( "WARNING: signal sampling is not uniform. Naive upsampling will be used for select channels" );
					
					resampler = new NaiveMultichannelSignalResampler();
					collectChannelSampling();
					
				}
								
			}
			
		}
			
		try {
			this.calibrationCapable = reader.is_calibration();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_callibration, assumed false");
			logger.debug("Caught exception was", e);
		}
		
		readLabels();
						
	}
	
	private void collectChannelSampling() {
		
		channelSampling = new float[channelCount];
		
		for( int i=0; i<channelCount; i++ ) {
			
			try {
				channelSampling[i] = reader.get_sampling_frequency(i);
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec didn't return channel sampling for channel [" + i + "] - assumed default");
				logger.debug("Caught exception was", e);
				channelSampling[i] = samplingFrequency;
			}
						
		}
				
	}

	private void readLabels() {
		
		labels = null;
		
		boolean hasNames = false;
		try {
			hasNames = reader.is_channel_names();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_channel_names, assumed false");
			logger.debug("Caught exception was", e);
		}
		if( hasNames ) {
			try {
				labels = reader.get_channel_names();
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec doesn't support get_channel_names");
				logger.debug("Caught exception was", e);
			}
		}
		
		if( labels == null ) {
			return;
		}
		
		int i;
		for( i=0; i<labels.length; i++ ) {
			
			labels[i] = Util.trimString(labels[i]);
			
			if( labels[i] == null || labels[i].length() == 0 ) {
				labels[i] = "L" + i;
			}
			
		}
		
		if( labels.length < channelCount ) {
			logger.debug("some labels missing");
			String[] newLabels = new String[channelCount];
			for( i=0; i<labels.length; i++ ) {
				newLabels[i] = labels[i];				
			}
			for( ; i<channelCount; i++ ) {
				newLabels[i] = "L" + i;
			}
			labels = newLabels;
		}
		
	}

	public SignalMLCodecReader getReader() {
		return reader;
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}
			
	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public boolean isCalibrationCapable() {
		return calibrationCapable;
	}	
	
	@Override
	public boolean isSamplingFrequencyCapable() {
		return samplingFrequencyCapable;
	}

	@Override
	public boolean isChannelCountCapable() {
		return channelCountCapable;
	}

	@Override
	public float getCalibration() {
		return calibration;
	}
	
	@Override
	public String getLabel(int channel) {
		if( channel < 0 || channel >= channelCount ) {
			throw new IndexOutOfBoundsException("Bad channel number [" + channel + "]");
		}
		if( labels == null ) {
			labels = new String[channelCount];
			for( int i=0; i<channelCount; i++ ) {
				labels[i] = "L" + (i+1);
			}
		}
		return labels[channel];
	}
	
	@Override
	public int getDocumentChannelIndex(int channel) {
		return channel;
	}

	@Override
	public int getSampleCount(int channel) {
		return sampleCount;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized( this ) {
			if( channel < 0 || channel >= channelCount ) {
				throw new IndexOutOfBoundsException("Bad channel number [" + channel + "]");
			}
			if( (signalOffset < 0) || ((signalOffset + count) > sampleCount) ) {
				throw new IndexOutOfBoundsException("Signal range [" + signalOffset + ":" + count + "] doesn't fit in the signal");			
			}
			if( (arrayOffset < 0) || ((arrayOffset + count) > target.length) ) {
				throw new IndexOutOfBoundsException("Target range [" + arrayOffset + ":" + count + "] doesn't fit in the target array");
			}
			if( uniformSampling || channelSampling[channel] == samplingFrequency ) {
				getRawSamples(channel, target, signalOffset, count, arrayOffset);
			} else { 
				resampler.resample(this, channel, target, signalOffset, count, arrayOffset, samplingFrequency, channelSampling[channel]);
			}
		}
	}

	@Override
	public void getRawSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized( this ) {
			int targetIndex = arrayOffset;
			int i = signalOffset;
			int limit = signalOffset + count;
			try {
				for( ; i<limit; i++ ) {
					target[targetIndex] = reader.getChannelSample(i, channel);
					targetIndex++;
				}
			} catch( SignalMLCodecException ex ) {
				logger.error("Failed to get sample, filling the rest with zero and exiting", ex);
				for( ; i<limit; i++ ) {
					target[targetIndex] = 0.0F;
					targetIndex++;
				}
				return;
			}
		}
	}
	
	@Override
	public void setCalibration(float calibration) {
		synchronized( this ) {
			if( this.calibration != calibration ) {
				float last = this.calibration;
				this.calibration = calibration;
				calibrationExternal = true;
				try {
					reader.set_calibration(calibration);
				} catch( SignalMLCodecException ex ) {
					logger.error("Failed to propagate calibration to the codec", ex);				
				}
				pcSupport.firePropertyChange(CALIBRATION_PROPERTY, new Float(last), new Float(calibration));
			}
		}
	}

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		synchronized( this ) {
			if( this.samplingFrequency != samplingFrequency ) {
				float last = this.samplingFrequency;
				this.samplingFrequency = samplingFrequency;
				samplingFrequencyExternal = true;
				try {
					reader.set_sampling_frequency(samplingFrequency);
				} catch( SignalMLCodecException ex ) {
					logger.error("Failed to propagate sampling frequency to the codec", ex);				
				}
				if( !uniformSampling ) {
					collectChannelSampling();
				}
				pcSupport.firePropertyChange(SAMPLING_FREQUENCY_PROPERTY, new Float(last), new Float(samplingFrequency));
			}
		}
	}
	
	@Override
	public void setChannelCount(int channelCount) {
		synchronized( this ) {
			if( this.channelCount != channelCount ) {
				int last = this.channelCount;
				this.channelCount = channelCount;
				channelCountExternal = true;
				try {
					reader.set_number_of_channels(channelCount);
				} catch( SignalMLCodecException ex ) {
					logger.error("Failed to propagate channel count to the codec", ex);				
				}			
				readLabels();
				if( !uniformSampling ) {
					collectChannelSampling();
				}				
				pcSupport.firePropertyChange(CHANNEL_COUNT_PROPERTY, last, channelCount);			
			}
		}
	}

	@Override
	public OriginalMultichannelSampleSource duplicate() throws SignalMLException {

		SignalMLCodecReader newReader = reader.getCodec().createReader();
		newReader.open(reader.getCurrentFilename());

		SignalMLCodecSampleSource duplicate = new SignalMLCodecSampleSource(newReader);
		if( channelCountExternal ) {
			duplicate.setChannelCount(channelCount);
		}
		if( samplingFrequencyExternal ) {
			duplicate.setSamplingFrequency(samplingFrequency);
		}
		if( calibrationExternal ) {
			duplicate.setCalibration(calibration);
		}
				
		return duplicate; 
			
	}
	
	@Override
	public void destroy() {
		reader.close();
		reader = null;
	}
	
}
