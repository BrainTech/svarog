package pl.edu.fuw.fid.signalanalysis.wavelet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

/**
 * @author ptr@mimuw.edu.pl
 */
public class WaveletPreview {

	public final double t_start, t_end;
	public final ObservableList<XYChart.Data<Number,Number>> waveletData;
	public final ObservableList<XYChart.Data<Number,Number>> signalData;

	public WaveletPreview(double t_start, double t_end) {
		this.t_start = t_start;
		this.t_end = t_end;
		this.waveletData = FXCollections.observableArrayList();
		this.signalData = FXCollections.observableArrayList();
	}

	public void addDataPoint(double t, double waveletValue, double signalValue) {
		waveletData.add(new XYChart.Data<Number,Number>(t, waveletValue));
		signalData.add(new XYChart.Data<Number,Number>(t, signalValue));
	}

	public ObservableList<XYChart.Series<Number, Number>> getDataSeries() {
		return FXCollections.observableArrayList(
			new XYChart.Series<Number, Number>(signalData),
			new XYChart.Series<Number, Number>(waveletData)
		);
	}

	public void scaleWavelet(double factor) {
		for (XYChart.Data<Number, Number> point : waveletData) {
			point.setYValue(point.getYValue().doubleValue() * factor);
		}
	}

}
