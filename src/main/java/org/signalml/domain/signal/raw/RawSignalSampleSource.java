/* RawSignalSampleSource.java created 2008-01-29
 * 
 */

package org.signalml.domain.signal.raw;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.AbstractMultichannelSampleSource;
import org.signalml.domain.signal.OriginalMultichannelSampleSource;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;

/** RawSignalSampleSource
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalSampleSource extends AbstractMultichannelSampleSource implements OriginalMultichannelSampleSource {

	protected static final Logger logger = Logger.getLogger(RawSignalSampleSource.class);
	
	private File file;
	private RandomAccessFile randomAccessFile;
	
	private int channelCount;
	private float samplingFrequency;
	
	private RawSignalSampleType sampleType;
	private RawSignalByteOrder byteOrder;
	
	private float calibration = 1F;
	
	private String[] labels;

	private byte[] byteBuffer;
	private int minBufferedSample;
	private int maxBufferedSample;
	private ByteBuffer bBuffer;
	
	private int sampleByteWidth;
	private int sampleCount;
		
	public RawSignalSampleSource(File file, int channelCount, float samplingFrequency, RawSignalSampleType sampleType, RawSignalByteOrder byteOrder) throws IOException {
		this.file = file;
		this.channelCount = channelCount;
		this.samplingFrequency = samplingFrequency;
		this.sampleType = sampleType;
		this.byteOrder = byteOrder;
		
		randomAccessFile = new RandomAccessFile(file, "r");
		sampleByteWidth = sampleType.getByteWidth();
		
		sampleCount = (int) (file.length() / ( channelCount * sampleByteWidth ));		
	}
	
	public void close() {
		if( randomAccessFile != null ) {
			try {
				randomAccessFile.close();
			} catch (IOException ex) {
				// ignore
			} finally {
				randomAccessFile = null;
			}
		}
	}

	@Override
	public OriginalMultichannelSampleSource duplicate() throws SignalMLException {

		RawSignalSampleSource newSource;
		try {
			newSource = new RawSignalSampleSource(file, channelCount, samplingFrequency, sampleType, byteOrder);
		} catch (IOException ex) {
			throw new SignalMLException(ex);
		}
		
		newSource.calibration = calibration;
		if( labels != null ) {
			newSource.labels = Arrays.copyOf(labels, labels.length);
		}
		
		return newSource;
		
	}
	
	public File getFile() {
		return file;
	}
	
	public int getSampleCount() {
		return sampleCount;
	}

	public RawSignalSampleType getSampleType() {
		return sampleType;
	}

	public RawSignalByteOrder getByteOrder() {
		return byteOrder;
	}

	@Override
	public boolean isCalibrationCapable() {
		return true;
	}

	@Override
	public boolean isChannelCountCapable() {
		return true;
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return true;
	}

	@Override
	public float getCalibration() {
		return calibration;
	}
	
	@Override
	public void setCalibration(float calibration) {
		if( this.calibration != calibration ) {
			float oldCalibration = this.calibration;
			this.calibration = calibration;
			
			pcSupport.firePropertyChange(CALIBRATION_PROPERTY, oldCalibration, calibration);
		}		
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}
	
	@Override
	public void setChannelCount(int channelCount) {
		throw new SanityCheckException( "Changing channel count not allowed" );
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}
	
	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		if( this.samplingFrequency != samplingFrequency ) {
			float oldSamplingFrequency = this.samplingFrequency;
			this.samplingFrequency = samplingFrequency;
			
			pcSupport.firePropertyChange(SAMPLING_FREQUENCY_PROPERTY, oldSamplingFrequency, samplingFrequency);
		}
	}
	
	public String[] getLabels() {
		return labels;
	}
	
	public void setLabels( String[] labels ) {
		if( this.labels != labels ) {
			String[] oldLabels = this.labels;
			this.labels = labels;
			
			pcSupport.firePropertyChange(LABEL_PROPERTY, oldLabels, labels);
		}
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return channel;
	}

	@Override
	public String getLabel(int channel) {
		if( labels != null && channel < labels.length ) {
			return labels[channel];
		}
		return "L" + (channel+1);
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
			
			int targetOffset;
			int sampleSize = channelCount * sampleByteWidth;
			
			// try to use existing mutiplexing buffer
			if( byteBuffer != null && minBufferedSample <= signalOffset && maxBufferedSample >= (signalOffset+count-1) ) {
				
				targetOffset = (signalOffset-minBufferedSample) * channelCount;
				
			} else {
				
				byteBuffer = new byte[count * sampleSize];
				minBufferedSample = signalOffset;
				maxBufferedSample = signalOffset + count - 1;
				
				try {
					randomAccessFile.seek(signalOffset * sampleSize);
					randomAccessFile.readFully(byteBuffer);
				} catch (IOException ex) {
					byteBuffer = null;
					logger.error("Failed to read samples, filling the array with zero and exiting", ex);
					for( int i=0; i<count; i++ ) {
						target[arrayOffset+i] = 0.0F;
					}
					return;
				}
			
				bBuffer = ByteBuffer.wrap(byteBuffer).order(byteOrder.getByteOrder());
				
				targetOffset = 0;
								
			}
			
			int sample = channel;
			int i;
			
			switch( sampleType ) {
			
			case DOUBLE :
				DoubleBuffer doubleBuffer = bBuffer.asDoubleBuffer();
				for( i=0; i<count; i++ ) {
					target[arrayOffset+i] = ( doubleBuffer.get(targetOffset+sample) * calibration );					
					sample += channelCount;
				}
				break;
				
			case FLOAT :
				FloatBuffer floatBuffer = bBuffer.asFloatBuffer();
				for( i=0; i<count; i++ ) {
					target[arrayOffset+i] = ( floatBuffer.get(targetOffset+sample) * calibration );					
					sample += channelCount;
				}
				break;
				
			case INT :
				IntBuffer intBuffer = bBuffer.asIntBuffer();
				for( i=0; i<count; i++ ) {
					target[arrayOffset+i] = ( intBuffer.get(targetOffset+sample) * calibration );					
					sample += channelCount;
				}
				break;

			case SHORT :
				ShortBuffer shortBuffer = bBuffer.asShortBuffer();
				for( i=0; i<count; i++ ) {
					target[arrayOffset+i] = ( shortBuffer.get(targetOffset+sample) * calibration );					
					sample += channelCount;
				}
				break;
				
			}
									
		}
		
	}

	@Override
	public void destroy() {
		close();
	}
	
}
