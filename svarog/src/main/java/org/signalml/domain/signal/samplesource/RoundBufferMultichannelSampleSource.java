/* RoundBufferMultichannelSampleSource.java
 *
 */

package org.signalml.domain.signal.samplesource;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import javax.swing.event.EventListenerList;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.change.events.PluginSignalChangeEvent;
import org.signalml.plugin.export.change.listeners.PluginSignalChangeListener;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.impl.change.events.PluginSignalChangeEventImpl;

public class RoundBufferMultichannelSampleSource extends DoubleArraySampleSource implements OriginalMultichannelSampleSource, ChangeableMultichannelSampleSource {

	/**
	 * The list containing objects listening for change in this sample source.
	 */
	private EventListenerList listenerList = new EventListenerList();

	protected int nextInsertPos;
	protected boolean full;
	protected DocumentView documentView;
	protected float samplingFrequency;
	protected Object[] labels;

	/**
	 * The calibration gain for the signal - the value by which each sample
	 * value is multiplied.
	 */
	private float[] calibrationGain;

	/**
	 * The calibration offset for the signal - the value which is added
	 * to each sample value.
	 */
	private float[] calibrationOffset;

	/**
	 * Semaphore preventing simultaneous read/write/newSamplesCount operations.
	 */
	private Semaphore semaphore;

	/**
	 * Stores the number of new samples - used by getNewSamplesCount().
	 */
	private int newSamplesCount = 0;

	public RoundBufferMultichannelSampleSource(int channelCount, int sampleCount) {

		super(null, channelCount, sampleCount);
		this.samples = new double[channelCount][sampleCount];
		nextInsertPos = 0;
		full = false;
		semaphore = new Semaphore(1);
	}

	public DocumentView getDocumentView() {
		return documentView;
	}

	public void setDocumentView(DocumentView documentView) {
		this.documentView = documentView;
	}

	synchronized int getNextInsertPos() {
		return nextInsertPos;
	}

	synchronized void setNextInsertPos(int nextInsertPos) {
		this.nextInsertPos = nextInsertPos;
	}

	synchronized boolean isFull() {
		return full;
	}

	synchronized void setFull(boolean full) {
		this.full = full;
	}

	synchronized double[][] getSamples() {
		return this.samples;
	}

	synchronized void setSamples(double[][] samples) {
		this.samples = samples;
	}

	protected synchronized void incrNextInsertPos() {

		nextInsertPos++;
		if (nextInsertPos == sampleCount) {
			full = true;
			nextInsertPos = 0;
		}

	}

	@Override
	public synchronized void addSampleChunk(float[] newSamples) {

		for (int i = 0; i < channelCount; i++) {
			samples[i][nextInsertPos] = newSamples[i];
		}
		incrNextInsertPos();
		newSamplesCount++;

		fireNewSamplesAddedEvent();
	}

	@Override
	public synchronized void addSamples(float[] newSamples) {

		addSampleChunk(newSamples);
		fireNewSamplesAddedEvent();

	}

	@Override
	public synchronized void addSamples(List<float[]> newSamples) {

		for (Iterator< float[]> i = newSamples.iterator(); i.hasNext();)
			addSampleChunk(i.next());
		fireNewSamplesAddedEvent();
	}

	// przy zwykłych źródłach sygnału sampleCount jest znany z góry, a tu nie;
	// tutaj sampleCount oznacza maksymalną ilość próbek w stanie gdy cały bufor już jest wypełniony
	// offset powinien być w ramach przedziału od zera do sampleCount-1 - count
	// jeśli bufor jest pusty to wszystkie próbki od zera do sampleCount - 1 są równe zero
	// jeśli bufor jest częściowo wypełniony, to próbki od zera do sampleCount - 1 - n są równe zero
	// a pozostałe nie, gdzie n jest liczbą próbek zaczytanych do bufora; jesli cały bufor jest wypełiony
	// to offset trzeba przesunąć odpowiednio względem bieżącego punktu wstawiania
	@Override
	public synchronized void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		getSamples(channel, target, signalOffset, count, arrayOffset, true);
	}

	public synchronized void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset, boolean calibrate) {
		double[] tmp = new double[sampleCount];
		if (full) {
			for (int i = 0; i < sampleCount; i++) {
				tmp[i] = samples[channel][(nextInsertPos + i) % sampleCount];
			}
		}
		else {
			if (nextInsertPos == 0) {
				for (int i = 0; i < sampleCount; i++) {
					tmp[i] = 0.0;
				}
			}
			else {
				int n = sampleCount - nextInsertPos;
				for (int i = 0; i < n; i++)
					tmp[i] = 0.0;
				for (int i = n; i < sampleCount; i++) {
					tmp[i] = samples[channel][i - n];
				}
			}
		}

		//calibration
		if (calibrate) {
			for (int i = 0; i < count; i++)
				target[arrayOffset+i] = calibrateSample(tmp[signalOffset + i], channel);
		}
		else {
			for (int i = 0; i < count; i++)
				target[arrayOffset+i] = tmp[signalOffset + i];
		}
	}

	protected double calibrateSample(double inputSampleValue, int channelIndex) {
		if (calibrationGain != null && calibrationOffset != null)
			return calibrationGain[channelIndex] * inputSampleValue + calibrationOffset[channelIndex];
		return inputSampleValue;
	}

	@Override
	public OriginalMultichannelSampleSource duplicate()
	throws SignalMLException {
		return null;
	}

	@Override
	public void setCalibrationGain(float calibration) {
	}

	@Override
	public void setChannelCount(int channelCount) {
	}

	@Override
	public String getLabel(int channel) {

		if (labels != null)
			return labels[channel].toString();
		else
			return super.getLabel(channel);

	}

	public Object[] getLabels() {
		return labels;
	}

	public void setLabels(Object[] labels) {
		this.labels = labels;
	}

	@Override
	public synchronized int getNewSamplesCount() {

		int x = newSamplesCount;
		clearNewSamplesCount();
		return x;

	}

	@Override
	public synchronized void clearNewSamplesCount() {
		newSamplesCount = 0;
	}

	@Override
	public void lock() {
		try {
			semaphore.acquire();
		} catch (InterruptedException ex) {
			java.util.logging.Logger.getLogger(RoundBufferMultichannelSampleSource.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void unlock() {
		semaphore.release();
	}

	/*
	 * Gets the number of received samples.
	 *
	 * @return number of samples received from the start
	 */
	public int getReceivedSampleCount() {
		if (full) {
			return sampleCount;
		} else {
			return nextInsertPos;
		}
	}

	@Override
	public boolean isCalibrationCapable() {
		return false;
	}

	@Override
	public float[] getCalibrationGain() {
		return calibrationGain;
	}

	@Override
	public boolean areIndividualChannelsCalibrationCapable() {
		return false;
	}

	@Override
	public void setCalibrationGain(float[] calibration) {
		this.calibrationGain = calibration;
	}

	@Override
	public float getSingleCalibrationGain() {
		return 1F;
	}

	/**
	 * Returns the number of samples per second
	 * @return the number of samples per second = 128
	 */
	@Override
	public float getSamplingFrequency() {
		return this.samplingFrequency;
	}

	/**
	 * Sets the number of samples per second
	 * @param sampling - sampling frequency
	 * @return Null
	 */
	@Override
	public void setSamplingFrequency(float sampling) {
		this.samplingFrequency = sampling;
	}

	/**
	 * Getting and setting calibration offset for RoundBufferMultichannelSampleSource
	 * is not supported yet.
	 * TODO: a clean-up is needed, and calibration gain/offset calculations
	 * should be performed in this class.
	 * @return (not supported)
	 */
	@Override
	public float[] getCalibrationOffset() {
		return calibrationOffset;
	}

	@Override
	public void setCalibrationOffset(float[] calibrationOffset) {
		this.calibrationOffset = calibrationOffset;
	}

	@Override
	public void setCalibrationOffset(float calibrationOffset) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public float getSingleCalibrationOffset() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void addSignalChangeListener(PluginSignalChangeListener listener) {
		listenerList.add(PluginSignalChangeListener.class, listener);
	}

	protected void fireNewSamplesAddedEvent() {
		Object[] listeners = listenerList.getListenerList();
		PluginSignalChangeEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==PluginSignalChangeListener.class) {
				if (e == null) {
					ExportedSignalDocument document = null;
					if (getDocumentView() != null)
						document = (ExportedSignalDocument) getDocumentView().getDocument();
					e = new PluginSignalChangeEventImpl(document);
				}
				((PluginSignalChangeListener)listeners[i+1]).newSamplesAdded(e);
			}
		}
	}

}
