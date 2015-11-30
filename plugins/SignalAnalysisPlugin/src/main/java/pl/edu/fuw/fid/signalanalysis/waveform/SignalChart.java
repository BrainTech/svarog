package pl.edu.fuw.fid.signalanalysis.waveform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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

	private static final String SIGNAL_STYLE = "-fx-stroke-width: 1; -fx-stroke: #000000;";
	private static final String WAVEFORM_STYLE = "-fx-stroke-width: 2; -fx-stroke: #FF0000;";

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
			createDataSeries(signalData, SIGNAL_STYLE),
			new XYChart.Series<Number, Number>(waveformData)
		);
		setAnimated(false);
		setCreateSymbols(false);
		setLegendVisible(false);
		setPrefHeight(100);
		setData(series);
	}

	private static XYChart.Series<Number, Number> createDataSeries(ObservableList<XYChart.Data<Number,Number>> data, final String style) {
		XYChart.Series<Number, Number> serie = new XYChart.Series<Number, Number>(data);
		serie.nodeProperty().addListener(new ChangeListener<Node>() {
			@Override
			public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
				if (newValue != null) {
					newValue.setStyle(style);
				}
			}
		});
		return serie;
	}

	public void setWaveform(Waveform wf, WindowType windowType, double t0, Complex coeff) {
		ObservableList<XYChart.Data<Number,Number>> waveformData = WaveformRenderer.compute(wf, t0, windowType, coeff, samplingFrequency);
		series.set(1, createDataSeries(waveformData, WAVEFORM_STYLE));
	}

	public void clearWaveform() {
		ObservableList<XYChart.Data<Number,Number>> waveformData = FXCollections.emptyObservableList();
		series.set(1, new XYChart.Series<Number, Number>(waveformData));
	}

}
