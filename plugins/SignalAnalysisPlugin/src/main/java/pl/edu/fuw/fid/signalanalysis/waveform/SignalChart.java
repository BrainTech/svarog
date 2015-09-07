package pl.edu.fuw.fid.signalanalysis.waveform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import org.apache.commons.math.complex.Complex;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class SignalChart extends LineChart<Number, Number> {

	private final double[] data;
	private final double samplingFrequency;
	private final ObservableList<XYChart.Series<Number,Number>> series;

	public SignalChart(SimpleSignal signal, Axis<Number> xAxis, Axis<Number> yAxis) {
		super(xAxis, yAxis);
		this.data = signal.getData();
		this.samplingFrequency = signal.getSamplingFrequency();

		ObservableList<XYChart.Data<Number,Number>> signalData = FXCollections.observableArrayList();
		for (int i=0; i<data.length; ++i) {
			double t = i / samplingFrequency;
			signalData.add(new XYChart.Data<Number,Number>(t, data[i]));
		}

		ObservableList<XYChart.Data<Number,Number>> waveformData = FXCollections.emptyObservableList();
		this.series = FXCollections.observableArrayList(
			new XYChart.Series<Number, Number>(signalData),
			new XYChart.Series<Number, Number>(waveformData)
		);
		setCreateSymbols(false);
		setLegendVisible(false);
		setPrefHeight(100);
		setData(series);
	}

	public void setWaveform(Waveform wf, WindowType windowType, double t0, Complex coeff) {
		ObservableList<XYChart.Data<Number,Number>> waveformData = WaveformRenderer.compute(wf, t0, windowType, coeff, samplingFrequency);
		series.set(1, new XYChart.Series<Number, Number>(waveformData));
	}

	public void clearWaveform() {
		ObservableList<XYChart.Data<Number,Number>> waveformData = FXCollections.emptyObservableList();
		series.set(1, new XYChart.Series<Number, Number>(waveformData));
	}

}
