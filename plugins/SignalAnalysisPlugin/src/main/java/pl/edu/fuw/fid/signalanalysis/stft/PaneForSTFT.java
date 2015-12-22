package pl.edu.fuw.fid.signalanalysis.stft;

import java.io.IOException;
import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import org.signalml.math.fft.WindowType;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ChannelSamples;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;
import pl.edu.fuw.fid.signalanalysis.SimpleSingleSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageChart;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageRefresher;
import pl.edu.fuw.fid.signalanalysis.waveform.SignalChart;
import pl.edu.fuw.fid.signalanalysis.waveform.TimeFrequency;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PaneForSTFT {

	final BorderPane root;
	final ObservableList<WindowType> windowTypeItems;
	final ObservableList<Integer> windowSizeItems;
	final ObservableList<WignerMapPalette> paletteTypeItems;
	final ImageChart chart;
	final ImageRendererForSTFT renderer;

	public PaneForSTFT(final SvarogAccessSignal signalAccess, ExportedSignalSelection selection) throws IOException, NoActiveObjectException {
		root = new BorderPane();
		windowTypeItems = FXCollections.observableArrayList(
			WindowType.BARTLETT,
			WindowType.GAUSSIAN,
			WindowType.HAMMING,
			WindowType.HANN,
			WindowType.RECTANGULAR,
			WindowType.WELCH
		);
		windowSizeItems = FXCollections.observableArrayList(
			32, 64, 128, 256, 512, 1024
		);
		paletteTypeItems = FXCollections.observableArrayList(
			WignerMapPalette.RAINBOW,
			WignerMapPalette.GRAYSCALE
		);

		URL url = getClass().getResource("SettingsPanelForSTFT.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(url);
		VBox settingsPanel = (VBox) fxmlLoader.load();

		final ComboBox windowType = (ComboBox) fxmlLoader.getNamespace().get("windowTypeComboBox");
		windowType.setItems(windowTypeItems);

		final ComboBox windowSize = (ComboBox) fxmlLoader.getNamespace().get("windowSizeComboBox");
		windowSize.setItems(windowSizeItems);
		windowSize.setEditable(true);

		final ComboBox paletteType = (ComboBox) fxmlLoader.getNamespace().get("paletteTypeComboBox");
		paletteType.setItems(paletteTypeItems);

		final int selectedChannel = Math.max(selection.getChannel(), 0);
		final ChannelSamples samples = signalAccess.getActiveProcessedSignalSamples(selectedChannel);
		final double samplingFrequency = samples.getSamplingFrequency();
		final double nyquistFrequency = 0.5 * samplingFrequency;

		double tMin = selection.getPosition();
		double tMax = selection.getEndPosition();
		final NumberAxis xAxis = new NumberAxis(tMin, tMax, 1.0);
		final NumberAxis yAxis = new NumberAxis(0.0, nyquistFrequency, 10.0);
		yAxis.setLabel("frequency [Hz]");
		yAxis.setPrefWidth(50);

		final Slider maxFrequency = (Slider) fxmlLoader.getNamespace().get("frequencySlider");
		maxFrequency.setMax(nyquistFrequency);
		maxFrequency.setValue(nyquistFrequency);
		maxFrequency.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double maxFreq = Math.round(newValue.doubleValue());
				yAxis.setUpperBound(maxFreq);
			}
		});

		SingleSignal signal = new SimpleSingleSignal(samples);
		renderer = new ImageRendererForSTFT(signal);
		final ImageRefresher refresher = new ImageRefresher(renderer);
		Runnable onResize = new Runnable() {
			@Override
			public void run() {
				refresher.refreshChartImage(chart);
			}
		};
		chart = new ImageChart(xAxis, yAxis, renderer, onResize);

		windowType.getSelectionModel().select(renderer.getWindowType());
		windowSize.getSelectionModel().select(renderer.getWindowLength());
		paletteType.getSelectionModel().select(renderer.getPaletteType());

		final NumberAxis xAxisSignal = new NumberAxis(tMin, tMax, 1.0);
		final NumberAxis yAxisSignal = new NumberAxis();
		xAxisSignal.setLabel("time [s]");
		yAxisSignal.setLabel("value [µV]");
		yAxisSignal.setPrefWidth(50);
		final SignalChart signalChart = new SignalChart(signal, tMin, tMax, xAxisSignal, yAxisSignal);

		windowType.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				WindowType wt = (WindowType) windowType.getSelectionModel().getSelectedItem();
				if (wt != null) {
					renderer.setWindowType(wt);
					refresher.refreshChartImage(chart);
				}
			}
		});

		windowSize.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				Object item = windowSize.getSelectionModel().getSelectedItem();
				Integer ws = SignalAnalysisTools.parsePositiveInteger(item);
				if (ws != null) {
					renderer.setWindowLength(ws);
					refresher.refreshChartImage(chart);
				}
			}
		});

		paletteType.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				WignerMapPalette pt = (WignerMapPalette) paletteType.getSelectionModel().getSelectedItem();
				if (pt != null) {
					renderer.setPaletteType(pt);
					refresher.refreshChartImage(chart);
				}
			}
		});

		final CheckBox padToHeight = (CheckBox) fxmlLoader.getNamespace().get("padToHeightCheckBox");
		padToHeight.setSelected(renderer.getPadToHeight());
		padToHeight.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				renderer.setPadToHeight(newValue);
				refresher.refreshChartImage(chart);
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
				Object item = windowSize.getSelectionModel().getSelectedItem();
				Integer ws = SignalAnalysisTools.parsePositiveInteger(item);
				TimeFrequency tf = chart.getTimeFrequency((int)event.getX(), (int)event.getY());
				if (ws != null && tf != null) {
					WindowType wt = (WindowType) windowType.getSelectionModel().getSelectedItem();
					signalChart.setWaveform(new SineWaveform(tf.f, 0.5*ws/samplingFrequency), wt, tf.t, tf.v);
				}
			}
		});

		BorderPane main = new BorderPane();
		main.setCenter(chart);
		main.setBottom(signalChart);

		root.setCenter(main);
		root.setLeft(settingsPanel);
	}

	public Pane getPane() {
		return root;
	}

}
