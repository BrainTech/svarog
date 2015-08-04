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
import javafx.scene.chart.LineChart;
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
import pl.edu.fuw.fid.signalanalysis.ImageChart;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

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
		xAxis.setLabel("time [s]");
		yAxis.setLabel("scale inverse [Hz]");
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

		final NumberAxis waveletX = new NumberAxis();
		final NumberAxis waveletY = new NumberAxis();
		final LineChart<Number, Number> waveletChart = new LineChart<Number, Number>(waveletX, waveletY);
		waveletChart.setCreateSymbols(false);
		waveletChart.setLegendVisible(false);
		waveletX.setAutoRanging(false);
		settingsPanel.getChildren().add(waveletChart);
		waveletChart.setVisible(false);

		chart.setOnCursorOffChart(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				waveletChart.setVisible(false);
			}
		});

		chart.setOnCursorOnChart(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double t = chart.getXAxis().getValueForDisplay(event.getX()).doubleValue();
				double f = chart.getYAxis().getValueForDisplay(event.getY()).doubleValue();
				WaveletPreview preview = renderer.computeWaveletPreview(t, 1.0/f);
				waveletX.setLowerBound(preview.t_start);
				waveletX.setUpperBound(preview.t_end);
				waveletChart.setData(preview.getDataSeries());
				waveletChart.setVisible(true);
			}
		});

		StackPane stack = new StackPane();
		stack.getChildren().addAll(
			chart,
			chart.getProgressIndicator()
		);
		stack.setAlignment(Pos.CENTER);
		root.setCenter(stack);
		root.setLeft(settingsPanel);
	}

	public Pane getPane() {
		return root;
	}

}
