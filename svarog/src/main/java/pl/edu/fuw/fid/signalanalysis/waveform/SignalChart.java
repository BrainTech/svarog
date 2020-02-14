package pl.edu.fuw.fid.signalanalysis.waveform;

import java.awt.Color;
import org.apache.commons.math.complex.Complex;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;

/**
 * Chart for the signal displayed in interactive time-frequency map window.
 * Allows to overlay waveform over the signal, in distinctive colour.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SignalChart extends JFreeChart {

	private final double samplingFrequency;

	private static enum SeriesID {
		SIGNAL,
		WAVEFORM
	}

	public SignalChart(SingleSignal signal, double tMin, double tMax) {
		super(createPlot(signal, tMin, tMax));
		samplingFrequency = signal.getSamplingFrequency();
		removeLegend();
	}

	public static XYPlot createPlot(SingleSignal signal, double tMin, double tMax) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		int nMin = (int) Math.round(tMin * signal.getSamplingFrequency());
		int nMax = (int) Math.round(tMax * signal.getSamplingFrequency());
		int length = nMax - nMin;
		double[][] data = new double[2][length];
		for (int n=nMin; n<nMax; ++n) {
			data[0][n-nMin] = (tMax - tMin) * (n - nMin) / length + tMin;
		}
		signal.getSamples(nMin, length, data[1]);
		dataset.addSeries(SeriesID.SIGNAL, data);

		double min = data[1][0], max = data[1][0];
		for (int n=1; n<length; ++n) {
			min = Math.min(min, data[1][n]);
			max = Math.max(max, data[1][n]);
		}

		NumberAxis xAxis = new NumberAxis("time (s)");
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setRange(tMin, tMax);
		NumberAxis yAxis = new NumberAxis("value (µV)");
		yAxis.setRange(min, max);
		yAxis.setFixedDimension(50);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.BLACK);

		return new XYPlot(dataset, xAxis, yAxis, renderer);
	}

	public void setWaveform(Waveform wf, WindowType windowType, double t0, Complex coeff) {
		double[][] waveformData = WaveformRenderer.compute(wf, t0, windowType, coeff, samplingFrequency);
		DefaultXYDataset dataset = (DefaultXYDataset) getXYPlot().getDataset();
		dataset.addSeries(SeriesID.WAVEFORM, waveformData);
	}

	public void clearWaveform() {
		DefaultXYDataset dataset = (DefaultXYDataset) getXYPlot().getDataset();
		dataset.removeSeries(SeriesID.WAVEFORM);
	}

}
