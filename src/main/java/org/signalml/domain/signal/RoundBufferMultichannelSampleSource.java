/* RoundBufferMultichannelSampleSource.java
 *
 */

package org.signalml.domain.signal;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;

import org.signalml.plugin.export.view.DocumentView;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.plugin.export.SignalMLException;


public class RoundBufferMultichannelSampleSource extends DoubleArraySampleSource implements OriginalMultichannelSampleSource, ChangeableMultichannelSampleSource {

	protected int nextInsertPos;
	protected boolean full;
	protected DocumentView documentView;
	protected Object[] labels;

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
	public synchronized void addSampleChunk(double[] newSamples) {

		for (int i = 0; i < channelCount; i++) {
			samples[i][nextInsertPos] = newSamples[i];
		}
		incrNextInsertPos();
		newSamplesCount++;

	}

	@Override
	public synchronized void addSamples(double[] newSamples) {

		addSampleChunk(newSamples);
		if (documentView != null && ((SignalView) documentView).getPlots() != null) {
			for (Iterator<SignalPlot> i = ((SignalView) documentView).getPlots().iterator(); i.hasNext();)
				i.next().repaint();
		}

	}

	@Override
	public synchronized void addSamples(List<double[]> newSamples) {

		for (Iterator< double[]> i = newSamples.iterator(); i.hasNext();)
			addSampleChunk(i.next());
		if (documentView != null && ((SignalView) documentView).getPlots() != null) {
			for (Iterator<SignalPlot> i = ((SignalView) documentView).getPlots().iterator(); i.hasNext();)
				i.next().repaint();
		}

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
		for (int i = 0; i < count; i++) {
			target[arrayOffset+i] = tmp[signalOffset + i];
		}
	}

	@Override
	public OriginalMultichannelSampleSource duplicate()
	throws SignalMLException {
		return null;
	}

	@Override
	public void setCalibration(float calibration) {
	}

	@Override
	public void setChannelCount(int channelCount) {
	}

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
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

}
