package pl.edu.fuw.fid.signalanalysis.wavelet;

import pl.edu.fuw.fid.signalanalysis.logaxis.LogarithmicAxis;
import java.io.IOException;
import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ChannelSamples;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageChart;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.SignalChart;
import pl.edu.fuw.fid.signalanalysis.waveform.TimeFrequency;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PaneForWavelet {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PaneForWavelet.class);

	final BorderPane root;
	final ObservableList<MotherWavelet> waveletTypeItems;
	final ObservableList<WignerMapPalette> paletteTypeItems;
	final ImageChart chart;
	final ImageRendererForWavelet renderer;

	public PaneForWavelet(SvarogAccessSignal signalAccess, ExportedSignalSelection selection) throws IOException, NoActiveObjectException {
		root = new BorderPane();
		paletteTypeItems = FXCollections.observableArrayList(
			WignerMapPalette.RAINBOW,
			WignerMapPalette.GRAYSCALE
		);
		waveletTypeItems = FXCollections.observableArrayList(
			new MexicanHatWavelet(),
			new GaborWavelet(),
			new ShannonWavelet()
		);

		URL url = getClass().getResource("SettingsPanelForWavelet.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(url);
		VBox settingsPanel = (VBox) fxmlLoader.load();

		final ComboBox paletteType = (ComboBox) fxmlLoader.getNamespace().get("paletteTypeComboBox");
		paletteType.setItems(paletteTypeItems);

		final ComboBox waveletType = (ComboBox) fxmlLoader.getNamespace().get("waveletTypeComboBox");
		waveletType.setItems(waveletTypeItems);
		waveletType.setConverter(new StringConverter() {
			@Override
			public String toString(Object object) {
				MotherWavelet wavelet = (MotherWavelet) object;
				return wavelet.getLabel();
			}

			@Override
			public Object fromString(String string) {
				logger.fatal("fromString called on StringConverter");
				return null;
			}
		});

		final double selectionLength = selection.getLength();
		final ChannelSamples samples = signalAccess.getActiveProcessedSignalSamples(selection.getChannel(), (float) selection.getPosition(), (float) selectionLength);
		final double samplingFrequency = samples.getSamplingFrequency();
		final double nyquistFrequency = 0.5 * samplingFrequency;

		final NumberAxis xAxis = new NumberAxis(0.0, selectionLength, 1.0);
		final LogarithmicAxis yAxis = new LogarithmicAxis();
		yAxis.setPrefWidth(50);
		yAxis.setLowerBound(5.0); // Hz
		yAxis.setUpperBound(nyquistFrequency);

		final Slider maxFrequency = (Slider) fxmlLoader.getNamespace().get("frequencySlider");
		maxFrequency.setMin(5.0); // Hz
		maxFrequency.setMax(nyquistFrequency);
		maxFrequency.setValue(nyquistFrequency);
		maxFrequency.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double maxFreq = Math.round(newValue.doubleValue());
				yAxis.setUpperBound(maxFreq);
			}
		});

		SimpleSignal signal = new SimpleSignal() {
			@Override
			public double[] getData() {
				return samples.getSamples();
			}

			@Override
			public double getSamplingFrequency() {
				return samplingFrequency;
			}
		};
		renderer = new ImageRendererForWavelet(signal);
		chart = new ImageChart(xAxis, yAxis, renderer);

		waveletType.getSelectionModel().select(renderer.getWavelet());
		paletteType.getSelectionModel().select(renderer.getPaletteType());

		final NumberAxis xAxisSignal = new NumberAxis(0.0, selectionLength, 1.0);
		final NumberAxis yAxisSignal = new NumberAxis();
		xAxisSignal.setLabel("time [s]");
		yAxisSignal.setLabel("value [µV]");
		yAxisSignal.setPrefWidth(50);
		final SignalChart signalChart = new SignalChart(signal, xAxisSignal, yAxisSignal);

		waveletType.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				MotherWavelet mv = (MotherWavelet) waveletType.getSelectionModel().getSelectedItem();
				if (mv != null) {
					renderer.setWavelet(mv);
					chart.refreshChartImage();
				}
			}
		});

		paletteType.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				WignerMapPalette pt = (WignerMapPalette) paletteType.getSelectionModel().getSelectedItem();
				if (pt != null) {
					renderer.setPaletteType(pt);
					chart.refreshChartImage();
				}
			}
		});

		chart.setOnCursorOffChart(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				signalChart.clearWaveform();
			}
		});

		chart.setOnCursorOnChart(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MotherWavelet mv = (MotherWavelet) waveletType.getSelectionModel().getSelectedItem();
				TimeFrequency tf = chart.getTimeFrequency((int)event.getX(), (int)event.getY());
				if (mv != null && tf != null) {
					signalChart.setWaveform(mv.scale(tf.f), null, tf.t, tf.v);
				}
			}
		});

		StackPane stack = new StackPane();
		stack.getChildren().addAll(
			chart,
			chart.getProgressIndicator()
		);
		stack.setAlignment(Pos.CENTER);

		BorderPane main = new BorderPane();
		main.setCenter(stack);
		main.setBottom(signalChart);
		
		root.setCenter(main);
		root.setLeft(settingsPanel);
	}

	public Pane getPane() {
		return root;
	}

}
