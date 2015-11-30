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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.signalml.app.document.signal.SignalDocument;
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
	final ObservableList<String> frequencyScaleItems;
	final ImageChart linChart, logChart;
	final ImageRendererForWavelet renderer;

	private ImageChart theChart;

	private MotherWavelet createWavelet(MotherWavelet wavelet, double param) {
		if (wavelet instanceof ParamWavelet) try {
			Class<? extends MotherWavelet> cl = wavelet.getClass();
			wavelet = cl.getConstructor(Double.class).newInstance(param);
		} catch (Exception ex) {
			logger.error("wavelet class does not have a proper constructor", ex);
		}
		return wavelet;
	}

	private void setWavelet(MotherWavelet wavelet, double param) {
		wavelet = createWavelet(wavelet, param);
		renderer.setWavelet(wavelet);
		theChart.refreshChartImage();
	}

	public PaneForWavelet(SvarogAccessSignal signalAccess, ExportedSignalSelection selection) throws IOException, NoActiveObjectException {
		root = new BorderPane();
		paletteTypeItems = FXCollections.observableArrayList(
			WignerMapPalette.RAINBOW,
			WignerMapPalette.GRAYSCALE
		);
		waveletTypeItems = FXCollections.observableArrayList(
			new GaborWavelet(1.0),
			new ShannonWavelet(),
			new HaarWavelet()
		);
		frequencyScaleItems = FXCollections.observableArrayList(
			"linear",
			"logarithmic"
		);

		URL url = getClass().getResource("SettingsPanelForWavelet.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(url);
		VBox settingsPanel = (VBox) fxmlLoader.load();

		final ComboBox paletteType = (ComboBox) fxmlLoader.getNamespace().get("paletteTypeComboBox");
		paletteType.setItems(paletteTypeItems);

		final CheckBox paletteInvert = (CheckBox) fxmlLoader.getNamespace().get("paletteInvertCheckBox");

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
		final int selectedChannel = Math.max(selection.getChannel(), 0);
		final ChannelSamples samples = signalAccess.getActiveProcessedSignalSamples(selectedChannel, (float) selection.getPosition(), (float) selectionLength);
		final double samplingFrequency = samples.getSamplingFrequency();
		final double nyquistFrequency = 0.5 * samplingFrequency;
		final double minFrequency = 2.0; // Hz

		final NumberAxis xAxis = new NumberAxis(0.0, selectionLength, 1.0);
		final NumberAxis linAxis = new NumberAxis(minFrequency, nyquistFrequency, 10.0);
		linAxis.setPrefWidth(50);
		final LogarithmicAxis logAxis = new LogarithmicAxis();
		logAxis.setPrefWidth(50);
		logAxis.setLowerBound(minFrequency); // Hz
		logAxis.setUpperBound(nyquistFrequency);

		final Slider maxFrequency = (Slider) fxmlLoader.getNamespace().get("frequencyMaxSlider");
		maxFrequency.setMin(minFrequency); // Hz
		maxFrequency.setMax(nyquistFrequency);
		maxFrequency.setValue(nyquistFrequency);
		maxFrequency.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double maxFreq = Math.round(newValue.doubleValue());
				linAxis.setUpperBound(maxFreq);
				logAxis.setUpperBound(maxFreq);
			}
		});

		final ComboBox freqScale = (ComboBox) fxmlLoader.getNamespace().get("frequencyScaleComboBox");
		freqScale.setItems(frequencyScaleItems);
		freqScale.getSelectionModel().select(0);
		freqScale.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				boolean logScale = newValue.equals("logarithmic");
				if (logScale) {
					linChart.setVisible(false);
					theChart = logChart;
				} else {
					logChart.setVisible(false);
					theChart = linChart;
				}
				theChart.hideImage();
				theChart.setVisible(true);
				theChart.refreshChartImage();
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
		linChart = new ImageChart(xAxis, linAxis, renderer);
		logChart = new ImageChart(xAxis, logAxis, renderer);
		linChart.setVisible(true);
		logChart.setVisible(false);
		theChart = linChart;

		waveletType.getSelectionModel().select(renderer.getWavelet());
		paletteType.getSelectionModel().select(renderer.getPaletteType());

		final NumberAxis xAxisSignal = new NumberAxis(0.0, selectionLength, 1.0);
		final NumberAxis yAxisSignal = new NumberAxis();
		xAxisSignal.setLabel("time [s]");
		yAxisSignal.setLabel("value [µV]");
		yAxisSignal.setPrefWidth(50);
		final SignalChart signalChart = new SignalChart(signal, xAxisSignal, yAxisSignal);

		final Slider waveletParam = (Slider) fxmlLoader.getNamespace().get("waveletParamSlider");
		waveletParam.setValue(GaborWavelet.DEFAULT_WIDTH);
		waveletParam.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				MotherWavelet wavelet = (MotherWavelet) waveletType.getSelectionModel().getSelectedItem();
				if (wavelet != null) {
					setWavelet(wavelet, newValue.doubleValue());
				}
			}
		});

		waveletType.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				double newValue = waveletParam.getValue();
				MotherWavelet wavelet = (MotherWavelet) waveletType.getSelectionModel().getSelectedItem();
				if (wavelet != null) {
					waveletParam.setDisable(!(wavelet instanceof ParamWavelet));
					setWavelet(wavelet, newValue);
				}
			}
		});

		paletteType.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				WignerMapPalette pt = (WignerMapPalette) paletteType.getSelectionModel().getSelectedItem();
				if (pt != null) {
					renderer.setPaletteType(pt);
					theChart.refreshChartImage();
				}
			}
		});

		paletteInvert.setOnAction(new EventHandler() {
			@Override
			public void handle(Event event) {
				boolean inverted = paletteInvert.isSelected();
				renderer.setInverted(inverted);
				theChart.refreshChartImage();
			}
		});

		for (final ImageChart chart : new ImageChart[] { linChart, logChart }) {
			chart.setOnCursorOffChart(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					signalChart.clearWaveform();
				}
			});

			chart.setOnCursorOnChart(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					double newValue = waveletParam.getValue();
					MotherWavelet mv = (MotherWavelet) waveletType.getSelectionModel().getSelectedItem();
					mv = createWavelet(mv, newValue);
					TimeFrequency tf = chart.getTimeFrequency((int)event.getX(), (int)event.getY());
					if (mv != null && tf != null) {
						signalChart.setWaveform(mv.scale(tf.f), null, tf.t, tf.v);
					}
				}
			});
		}

		StackPane stack = new StackPane();
		stack.getChildren().addAll(
			linChart,
			logChart,
			linChart.getProgressIndicator(),
			logChart.getProgressIndicator()
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
